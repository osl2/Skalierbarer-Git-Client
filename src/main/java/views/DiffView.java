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

public class DiffView implements IDiffView {
  private Diff diff = new Diff();
  private JTextPane pane = new JTextPane();
  private SimpleAttributeSet addLine = new SimpleAttributeSet();
  private SimpleAttributeSet removeLine = new SimpleAttributeSet();
  private SimpleAttributeSet normalLine = new SimpleAttributeSet();


  public DiffView() {
    StyleConstants.setForeground(addLine, Color.GREEN);
    StyleConstants.setForeground(removeLine, Color.RED);
    StyleConstants.setForeground(normalLine, Color.BLACK);
  }
  /**
   * Opens the difference between the given file and and the previous version of the file.
   *
   * @param fileName the name of the given file.
   */
  public void openDiff(String fileName) {
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
    String[] output = diff.diffGit();
    pane.setText("");
    for(int i = 0; i < output.length; i++) {
      Document doc = pane.getStyledDocument();
      if(output[i].substring(0, 1).compareTo("+") == 0) {
        try {
          doc.insertString(doc.getLength(), output[i] + System.lineSeparator(), addLine);
        } catch (BadLocationException e) {
          e.printStackTrace();
        }
      } else if(output[i].substring(0, 1).compareTo("-") == 0) {
        try {
          doc.insertString(doc.getLength(), output[i] + System.lineSeparator(), removeLine);
        } catch (BadLocationException e) {
          e.printStackTrace();
        }
      } else {
        try {
          doc.insertString(doc.getLength(), output[i] + System.lineSeparator(), normalLine);
        } catch (BadLocationException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Remove the text displayed.
   */
  public void setNotVisible() {
    pane.setText("");
  }
}
