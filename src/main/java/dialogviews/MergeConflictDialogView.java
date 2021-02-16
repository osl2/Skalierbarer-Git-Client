package dialogviews;

import git.GitChangeConflict;
import git.GitFile;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
    private JScrollPane leftScrollbar;
    private JScrollPane centerScrollbar;
    private JScrollPane rightScrollbar;
    private String[] baseVersion;
    private List<GitChangeConflict> conflicts;

    public MergeConflictDialogView(GitFile file, Map<GitFile, List<GitChangeConflict>> conflictMap) {
        this.conflicts = conflictMap.get(file);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            baseVersion = br.lines().toArray(String[]::new);
            populatePanels(baseVersion);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private StyleContext getStyleContext() {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        Style conflictStyle = sc.addStyle("conflict", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
        StyleConstants.setBackground(conflictStyle, Color.lightGray);
        return sc;
    }

    private void populatePanels(String[] base) {
        StyleContext style = getStyleContext();
        Style conflictStyle = style.getStyle("conflict");
        DefaultStyledDocument leftText = new DefaultStyledDocument(style);
        DefaultStyledDocument rightText = new DefaultStyledDocument(style);
        DefaultStyledDocument centerText = new DefaultStyledDocument(style);


        Map<Integer, GitChangeConflict> changeIndex = new HashMap<>();
        conflicts.forEach(c -> changeIndex.put(c.getConflictStartLine(), c));

        for (int i = 0; i < base.length; i++) {
            try {
                if (changeIndex.containsKey(i)) {

                    leftText.insertString(leftText.getLength(), changeIndex.get(i).getOptionA(), conflictStyle);
                    leftText.insertString(leftText.getLength(), System.lineSeparator(), null);
                    rightText.insertString(rightText.getLength(), changeIndex.get(i).getOptionB(), conflictStyle);
                    rightText.insertString(rightText.getLength(), System.lineSeparator(), null);
                    centerText.insertString(centerText.getLength(), "----------------", conflictStyle);
                    centerText.insertString(centerText.getLength(), System.lineSeparator(), null);

                    // Skip conflict markers in file
                    i += changeIndex.get(i).getLength() + 1;

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

    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Resolve Merge Conflicts";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(1200, 900);
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
        // Intentional NOOP.
        // todo: re-evaluate
    }
}
