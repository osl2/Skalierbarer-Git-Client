package dialogviews;

import git.GitChangeConflict;
import git.GitFile;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
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
    private JTextArea centerTextArea;
    private JTextPane rightTextPane;
    private JLabel leftLabel;
    private JLabel centerLabel;
    private JLabel rightLabel;
    private JScrollPane leftSrollbar;
    private JScrollPane centerScrollbar;
    private JScrollPane rightScrollbar;
    private String[] baseVersion;
    private List<GitChangeConflict> conflicts;

    public MergeConflictDialogView(GitFile file, Map<GitFile, List<GitChangeConflict>> conflictMap) {
        this.conflicts = conflictMap.get(file);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            baseVersion = br.lines().toArray(String[]::new);
            leftTextPane.setDocument(getLeftText(baseVersion));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private StyledDocument getLeftText(String[] base) {
        DefaultStyledDocument doc = new DefaultStyledDocument();
        Map<Integer, GitChangeConflict> changeIndex = new HashMap<>();
        conflicts.forEach(c -> changeIndex.put(c.getConflictStartLine(), c));

        for (int i = 0; i < base.length; i++) {
            if (changeIndex.containsKey(i)) {
                // Do replacement
            } else {
                // Append line normally.
                try {
                    doc.insertString(doc.getLength(), base[i], null);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
        return doc;
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
        // Intentional NOOP.
        // todo: re-evaluate
    }
}
