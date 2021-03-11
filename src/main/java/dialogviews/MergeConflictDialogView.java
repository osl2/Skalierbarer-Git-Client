package dialogviews;

import controller.GUIController;
import git.GitFileConflict;
import git.conflict.AbstractHunk;
import git.conflict.ConflictHunk;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Iterator;

/**
 * This View is used to help the user resolve any file conflicts
 */
public class MergeConflictDialogView implements IDialogView {
    private final GitFileConflict fileConflict;
    private JPanel contentPane;
    private JTextPane leftTextPane;
    private JTextPane centerTextArea;
    private JTextPane rightTextPane;
    private JLabel leftLabel;
    private JLabel centerLabel;
    private JLabel rightLabel;
    @SuppressWarnings("unused")
    private JScrollPane leftScrollbar;
    private JButton buttonLeftAccept;
    private JButton buttonLeftDecline;
    private JButton buttonRightAccept;
    private JButton buttonRightDecline;
    private JButton okButton;
    @SuppressWarnings("unused")
    private JScrollPane centerScrollbar;
    @SuppressWarnings("unused")
    private JScrollPane rightScrollbar;
    @SuppressWarnings("unused")
    private JPanel comparisonPane;
    private int sidesHandled = 0;
    private final Iterator<ConflictHunk> conflictHunkIterator;
    private ConflictHunk currentHunk;

    /**
     * Creates a new MergeConflictDialogView
     *
     * @param fileConflict  The {@link GitFileConflict} encapsulating the conflicting changes for that file
     * @param titleOurs     Title to be shown over the left side view, usually the branch you merge into
     * @param titleTheirs   Title to be shown over the right side view, usually the branch you merge from
     */
    public MergeConflictDialogView(GitFileConflict fileConflict,
                                   String titleOurs, String titleTheirs) {
        nameComponents();
        this.fileConflict = fileConflict;
        this.conflictHunkIterator = fileConflict.getConflictHunkList().listIterator();
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
        this.buttonLeftAccept.addActionListener(e -> buttonListener(getActiveHunk(), ButtonAction.ACCEPT_OURS));
        this.buttonLeftDecline.addActionListener(e -> buttonListener(getActiveHunk(), ButtonAction.DECLINE_OURS));
        this.buttonRightAccept.addActionListener(e -> buttonListener(getActiveHunk(), ButtonAction.ACCEPT_THEIRS));
        this.buttonRightDecline.addActionListener(e -> buttonListener(getActiveHunk(), ButtonAction.DECLINED_THEIRS));

        handleNextConflict();
        populatePanels();
    }

    /**
     * This method is needed in order to execute the GUI tests successfully.
     * Do not change otherwise tests might fail.
     */
    private void nameComponents() {
        contentPane.setName("contentPane");
        leftTextPane.setName("leftTextPane");
        centerTextArea.setName("centerTextArea");
        rightTextPane.setName("rightTextPane");
        leftLabel.setName("leftLabel");
        centerLabel.setName("centerLabel");
        rightLabel.setName("rightLabel");
        leftScrollbar.setName("leftScrollbar");
        buttonLeftAccept.setName("buttonLeftAccept");
        buttonLeftDecline.setName("buttonLeftDecline");
        buttonRightAccept.setName("buttonRightAccept");
        buttonRightDecline.setName("buttonRightDecline");
        okButton.setName("okButton");
        centerScrollbar.setName("centerScrollbar");
        rightScrollbar.setName("rightScrollbar");
        comparisonPane.setName("comparisonPane");
    }

    private ConflictHunk getActiveHunk() {
        return currentHunk;
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
        GUIController.getInstance().closeDialogView();
    }

    private void buttonListener(ConflictHunk hunk, ButtonAction btnAction) {
        switch (btnAction) {
            case ACCEPT_OURS:
                hunk.acceptOurs();
                // fall through
            case DECLINE_OURS:
                buttonLeftAccept.setEnabled(false);
                buttonLeftDecline.setEnabled(false);
                break;
            case ACCEPT_THEIRS:
                hunk.acceptTheirs();
                // fall through
            case DECLINED_THEIRS:
                buttonRightAccept.setEnabled(false);
                buttonRightDecline.setEnabled(false);
                break;
        }
        sidesHandled++;
        if (sidesHandled == 2 || fileConflict.wasDeleted()) {
            // If we do decline both sides, the conflict is not yet resolved. In that case we actively state that we
            // do not want to accept any side.
            if (!hunk.isResolved())
                hunk.acceptNone();
            buttonRightAccept.setEnabled(false);
            buttonLeftAccept.setEnabled(false);
            buttonLeftDecline.setEnabled(false);
            buttonRightDecline.setEnabled(false);
            handleNextConflict();
            sidesHandled = 0;
        }
        populatePanels();

    }

    private void handleNextConflict() {
        if (conflictHunkIterator.hasNext()) {
            this.currentHunk = conflictHunkIterator.next();
            buttonRightAccept.setEnabled(true);
            buttonLeftAccept.setEnabled(true);
            buttonLeftDecline.setEnabled(true);
            buttonRightDecline.setEnabled(true);
            okButton.setEnabled(false);
        } else {
            this.okButton.setEnabled(true);
        }
    }


    private Style getApplicableStyle(StyleContext context, AbstractHunk hunk) {
        Style activeConflictStyle = context.getStyle("activeConflict");
        Style resolvedConflictStyle = context.getStyle("resolvedConflict");
        if (hunk instanceof ConflictHunk && hunk.isResolved()) {
            return resolvedConflictStyle;
        }
        if (hunk.isResolved()) {
            return context.getStyle(StyleContext.DEFAULT_STYLE);
        }
        return activeConflictStyle;
    }

    private void populatePanels() {
        StyleContext style = getStyleContext();
        DefaultStyledDocument leftText = new DefaultStyledDocument(style);
        DefaultStyledDocument rightText = new DefaultStyledDocument(style);
        DefaultStyledDocument centerText = new DefaultStyledDocument(style);

        for (AbstractHunk hunk : fileConflict.getHunkList()) {
            Style applicableStyle = getApplicableStyle(style, hunk);

            try {
                if (hunk.isResolved()) {
                    for (String line : hunk.getLines()) {
                        centerText.insertString(centerText.getLength(), line + System.lineSeparator(), applicableStyle);
                    }
                } else {
                    centerText.insertString(centerText.getLength(), "------" + System.lineSeparator(), applicableStyle);
                }
                for (String line : hunk.getOurs()) {
                    leftText.insertString(leftText.getLength(), line + System.lineSeparator(), applicableStyle);
                }
                for (String line : hunk.getTheirs()) {
                    rightText.insertString(rightText.getLength(), line + System.lineSeparator(), applicableStyle);
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


    @Override
    public String getTitle() {
        return "Resolve Merge Conflicts (" + this.fileConflict.getGitFile().getPath().toString() + ")";
    }

    @Override
    public Dimension getDimension() {
        return contentPane.getPreferredSize();
    }

    @Override
    public JPanel getPanel() {
        return contentPane;
    }

    @Override
    public void update() {
        populatePanels();
    }

    private enum ButtonAction {ACCEPT_OURS, ACCEPT_THEIRS, DECLINE_OURS, DECLINED_THEIRS}

}