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
                file.rm(); // Add remove to the merge commit
                GUIController.getInstance().closeDialogView();

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
                    i += localConflictMap.get(i).getLength() + 1;
                } else {
                    bw.write(baseVersion[i]);
                    bw.write(System.lineSeparator());
                }
            }

            bw.flush();
            bw.close();

            try {
                file.add(); // Add file to the merge commit
                GUIController.getInstance().closeDialogView();
            } catch (GitException e) {
                GUIController.getInstance().errorHandler(e);
            }

        } catch (IOException e) {
            GUIController.getInstance().errorHandler(e);
        }
    }

    private void buttonListener(GitChangeConflict c, ButtonAction btnAction) {
        switch (btnAction) {
            case ACCEPT_OURS:
                c.acceptOurs();
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
            buttonRightAccept.setEnabled(false);
            buttonLeftAccept.setEnabled(false);
            buttonLeftDecline.setEnabled(false);
            buttonRightDecline.setEnabled(false);
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
        //return new Dimension(1400, 900);
        return contentPane.getPreferredSize();
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 5, new Insets(0, 10, 5, 10), -1, -1));
        contentPane.setPreferredSize(new Dimension(1500, 900));
        leftScrollbar = new JScrollPane();
        contentPane.add(leftScrollbar, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(400, -1), null, new Dimension(400, -1), 0, false));
        leftTextPane = new JTextPane();
        leftTextPane.setMaximumSize(new Dimension(2147483647, 2147483647));
        leftTextPane.setPreferredSize(new Dimension(400, 7));
        leftScrollbar.setViewportView(leftTextPane);
        centerScrollbar = new JScrollPane();
        contentPane.add(centerScrollbar, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(400, -1), null, new Dimension(400, -1), 0, false));
        centerTextArea = new JTextPane();
        centerTextArea.setMaximumSize(new Dimension(2147483647, 2147483647));
        centerTextArea.setPreferredSize(new Dimension(400, 21));
        centerScrollbar.setViewportView(centerTextArea);
        rightScrollbar = new JScrollPane();
        contentPane.add(rightScrollbar, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(400, -1), null, new Dimension(400, -1), 0, false));
        rightTextPane = new JTextPane();
        rightTextPane.setMaximumSize(new Dimension(400, 2147483647));
        rightTextPane.setPreferredSize(new Dimension(400, 21));
        rightTextPane.setRequestFocusEnabled(false);
        rightScrollbar.setViewportView(rightTextPane);
        leftLabel = new JLabel();
        leftLabel.setText("Label");
        contentPane.add(leftLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        centerLabel = new JLabel();
        centerLabel.setText("Label");
        contentPane.add(centerLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rightLabel = new JLabel();
        rightLabel.setText("Label");
        contentPane.add(rightLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(40, -1), 0, false));
        buttonLeftAccept = new JButton();
        buttonLeftAccept.setText("Button");
        panel1.add(buttonLeftAccept, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonLeftDecline = new JButton();
        buttonLeftDecline.setText("Button");
        panel1.add(buttonLeftDecline, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(40, -1), 0, false));
        buttonRightAccept = new JButton();
        buttonRightAccept.setText("Button");
        panel2.add(buttonRightAccept, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonRightDecline = new JButton();
        buttonRightDecline.setText("Button");
        panel2.add(buttonRightDecline, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        okButton = new JButton();
        okButton.setText("Button");
        panel3.add(okButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        panel3.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private enum ButtonAction {ACCEPT_OURS, ACCEPT_THEIRS, DECLINE_OURS, DECLINED_THEIRS}

}