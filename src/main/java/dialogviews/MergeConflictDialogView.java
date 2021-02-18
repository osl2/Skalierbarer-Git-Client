package dialogviews;

import controller.GUIController;
import git.GitChangeConflict;
import git.GitFile;
import git.exception.GitException;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This View is used to help the user resolve any conflicts during a Merge (or rebase for that matter)
 */
public class MergeConflictDialogView implements IDialogView {
    private JPanel contentPane;
    private JTextPane leftTextPane;
    private JTextPane centerTextArea;
    private JTextPane rightTextPane;
    private JLabel leftLabel;
    private JLabel centerLabel;
    private JLabel rightLabel;
    private final GitFile file;
    private final Map<Integer, GitChangeConflict> localConflictMap = new HashMap<>();
    @SuppressWarnings("unused")
    private JScrollPane leftScrollbar;
    private JButton buttonLeftAccept;
    private JButton buttonLeftDecline;
    private JButton buttonRightAccept;
    private JButton buttonRightDecline;
    private JButton okButton;
    @SuppressWarnings("unused")
    private JScrollPane centerScrollbar;
    private String[] baseVersion;
    @SuppressWarnings("unused")
    private JScrollPane rightScrollbar;
    private int activeConflictIndex = -1;
    private int sidesHandled = 0;

    public MergeConflictDialogView(GitFile file, Map<GitFile, List<GitChangeConflict>> conflictMap,
                                   String titleOurs, String titleTheirs) {
        List<GitChangeConflict> conflicts = conflictMap.get(file);
        this.file = file;
        this.buttonLeftAccept.setText(">");
        this.buttonRightAccept.setText("<");
        this.buttonLeftDecline.setText("X");
        this.buttonRightDecline.setText("X");
        this.okButton.setText("Merge");
        this.okButton.setEnabled(false);
        this.leftLabel.setText(titleOurs);
        this.rightLabel.setText(titleTheirs);
        this.centerLabel.setText("Result");
        this.okButton.addActionListener(e -> okButtonListener());
        conflicts.forEach(c -> localConflictMap.put(c.getConflictStartLine(), c));

        try {
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            baseVersion = br.lines().toArray(String[]::new);
            handleNextConflict();
            populatePanels(baseVersion);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private GitChangeConflict getActiveConflict() {
        return localConflictMap.get(activeConflictIndex);
    }

    private StyleContext getStyleContext() {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        Style conflictStyle = sc.addStyle("conflict", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
        Style activeConflictStyle = sc.addStyle("activeConflict", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
        Style resolvedConflictStyle = sc.addStyle("resolvedConflict", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
        StyleConstants.setBackground(conflictStyle, Color.lightGray);
        StyleConstants.setBackground(resolvedConflictStyle, Color.green);
        StyleConstants.setBackground(activeConflictStyle, Color.yellow);

        return sc;
    }

    private void okButtonListener() {
        if (localConflictMap.values().stream().anyMatch(GitChangeConflict::isDeleted)) {
            // This file needs to be deleted.
            if (!file.getPath().delete()) {
                GUIController.getInstance().errorHandler(file.getPath().getPath() + " konnte nicht gelöscht werden!");
                GUIController.getInstance().closeDialogView();
            }

            try {
                file.add(); // Add file to the merge commit
            } catch (GitException e) {
                GUIController.getInstance().errorHandler(e);
            }
            return;

        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getPath(), false));
            for (int i = 0; i < baseVersion.length; i++) {
                if (localConflictMap.containsKey(i)) {
                    // Apply changes
                    bw.write(localConflictMap.get(i).getResult());
                } else {
                    bw.write(baseVersion[i]);
                    bw.write(System.lineSeparator());
                }
            }

            bw.flush();
            bw.close();

        } catch (IOException e) {
            GUIController.getInstance().errorHandler(e);
        }
    }

    private void buttonListener(GitChangeConflict c, ButtonAction btnAction) {
        switch (btnAction) {
            case ACCEPT_OURS:
                c.accecptOurs();
                buttonLeftAccept.setEnabled(false);
                // fall through
            case DECLINE_OURS:
                buttonLeftAccept.setEnabled(false);
                buttonLeftDecline.setEnabled(false);
                break;
            case ACCEPT_THEIRS:
                c.acceptTheirs();
                // fall through
            case DECLINED_THEIRS:
                buttonRightAccept.setEnabled(false);
                buttonRightDecline.setEnabled(false);
                break;
        }
        sidesHandled++;
        if (sidesHandled == 2 || c.isResolved()) {
            handleNextConflict();
            sidesHandled = 0;
        }
        populatePanels(baseVersion);

    }

    private void handleNextConflict() {
        if (getActiveConflict() != null)
            this.getActiveConflict().resolve();

        int newIndex = -1;
        for (Integer possibleKey : this.localConflictMap.keySet()) {
            if (possibleKey <= this.activeConflictIndex) continue;
            newIndex = possibleKey;
            break;
        }

        if (newIndex == -1) {
            // todo: unlock ok button
            System.out.println("Conflicts resolved");
            this.okButton.setEnabled(true);
        } else {
            this.activeConflictIndex = newIndex;
            buttonRightAccept.setEnabled(true);
            buttonLeftAccept.setEnabled(true);
            buttonLeftDecline.setEnabled(true);
            buttonRightDecline.setEnabled(true);
            okButton.setEnabled(false);

            this.buttonLeftAccept.addActionListener(e -> buttonListener(getActiveConflict(), ButtonAction.ACCEPT_OURS));
            this.buttonLeftDecline.addActionListener(e -> buttonListener(getActiveConflict(), ButtonAction.DECLINE_OURS));
            this.buttonRightAccept.addActionListener(e -> buttonListener(getActiveConflict(), ButtonAction.ACCEPT_THEIRS));
            this.buttonRightDecline.addActionListener(e -> buttonListener(getActiveConflict(), ButtonAction.DECLINED_THEIRS));

            // todo : Scroll to next conflict
        }
    }

    private void populatePanels(String[] base) {
        StyleContext style = getStyleContext();
        Style conflictStyle = style.getStyle("conflict");
        Style activeConflictStyle = style.getStyle("activeConflict");
        Style resolvedConflictStyle = style.getStyle("resolvedConflict");
        DefaultStyledDocument leftText = new DefaultStyledDocument(style);
        DefaultStyledDocument rightText = new DefaultStyledDocument(style);
        DefaultStyledDocument centerText = new DefaultStyledDocument(style);


        for (int i = 0; i < base.length; i++) {
            try {
                if (localConflictMap.containsKey(i)) {
                    GitChangeConflict conflict = localConflictMap.get(i);
                    Style applicableStyle = (conflict.equals(getActiveConflict())) ? activeConflictStyle : conflictStyle;

                    if (conflict.isResolved()) {
                        // Insert resolution in center
                        applicableStyle = resolvedConflictStyle;
                        centerText.insertString(centerText.getLength(), conflict.getResult(), applicableStyle);
                    } else {
                        if (conflict.getResult() != null)
                            centerText.insertString(centerText.getLength(), conflict.getResult(), applicableStyle);
                        else
                            centerText.insertString(centerText.getLength(), "----------------", applicableStyle);
                        centerText.insertString(centerText.getLength(), System.lineSeparator(), null);

                    }
                    leftText.insertString(leftText.getLength(), conflict.getOptionOurs(), applicableStyle);
                    leftText.insertString(leftText.getLength(), System.lineSeparator(), null);

                    rightText.insertString(rightText.getLength(), conflict.getOptionTheirs(), applicableStyle);
                    rightText.insertString(rightText.getLength(), System.lineSeparator(), null);


                    // This file has been deleted in one of two branches
                    if (i == 0 && conflict.getLength() == -1)
                        break;

                    // Skip conflict markers in file
                    i += localConflictMap.get(i).getLength() + 1;

                } else {
                    // Append line normally.
                    leftText.insertString(leftText.getLength(), base[i], null);
                    leftText.insertString(leftText.getLength(), System.lineSeparator(), null);
                    rightText.insertString(rightText.getLength(), base[i], null);
                    rightText.insertString(rightText.getLength(), System.lineSeparator(), null);
                    centerText.insertString(centerText.getLength(), base[i], null);
                    centerText.insertString(centerText.getLength(), System.lineSeparator(), null);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        this.leftTextPane.setDocument(leftText);
        this.rightTextPane.setDocument(rightText);
        this.centerTextArea.setDocument(centerText);
        this.leftTextPane.revalidate();
        this.rightTextPane.revalidate();
        this.centerTextArea.revalidate();

    }


    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Resolve Merge Conflicts (" + this.file.getPath().toString() + ")";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(1400, 900);
        //return contentPane.getPreferredSize();
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return contentPane;
    }

    /**
     * Refresh the contents of the Dialog window,
     * i.e. when data changes
     */
    @Override
    public void update() {
        populatePanels(baseVersion);
        // Intentional NOOP.
        // todo: re-evaluate
    }

    private enum ButtonAction {ACCEPT_OURS, ACCEPT_THEIRS, DECLINE_OURS, DECLINED_THEIRS}

}