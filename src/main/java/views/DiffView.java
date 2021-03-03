package views;

import commands.Diff;
import git.GitCommit;
import git.GitFile;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.logging.Logger;

public class DiffView implements IDiffView {
  private final Diff diff = new Diff();
  private final JTextPane pane = new JTextPane();
  private final SimpleAttributeSet addLine = new SimpleAttributeSet();
  private final SimpleAttributeSet removeLine = new SimpleAttributeSet();
  private final SimpleAttributeSet normalLine = new SimpleAttributeSet();


  public DiffView() {
    StyleConstants.setForeground(addLine, Color.GREEN);
    StyleConstants.setForeground(removeLine, Color.RED);
    StyleConstants.setForeground(normalLine, Color.BLACK);
  }


  public JTextPane openDiffView() {
    return pane;
  }

  /**
   * Opens the difference between the given commit and the previous one.
   * Only the git diff of the given File will be displayed.
   *
   * @param activeCommit is the commit to compare to its previous version.
   * @param file is a the File of the given commit.
   */
  public void setDiff(GitCommit activeCommit, GitFile file) {
    diff.setDiffCommit(activeCommit, file);
    diff.execute();
    writeDiff();
  }

  public void setDiff(GitFile file) {
    diff.setDiffFile(file);
    diff.execute();
    writeDiff();
  }

  /**
   * Remove the text displayed.
   */
  public void setNotVisible() {
    pane.setText("");
  }

  /**
   * Writes the output of diff.diffGit() in the JTextPane. Lines leading with + will be displayed
   * green and lines leading with - will be displayed red.
   */
  private void writeDiff() {
    String[] output = diff.diffGit();
    pane.setText("");
    for (String s : output) {
      Document doc = pane.getStyledDocument();
      if (s.substring(0, 1).compareTo("+") == 0) {
        try {
          doc.insertString(doc.getLength(), s + System.lineSeparator(), addLine);
        } catch (BadLocationException e) {
          Logger.getGlobal().warning(e.getMessage());
        }
      } else if (s.substring(0, 1).compareTo("-") == 0) {
        try {
          doc.insertString(doc.getLength(), s + System.lineSeparator(), removeLine);
        } catch (BadLocationException e) {
          Logger.getGlobal().warning(e.getMessage());
        }
      } else {
        try {
          doc.insertString(doc.getLength(), s + System.lineSeparator(), normalLine);
        } catch (BadLocationException e) {
          Logger.getGlobal().warning(e.getMessage());
        }
      }
    }
  }
}
