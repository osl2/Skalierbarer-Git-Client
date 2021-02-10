package views;

import commands.Diff;
import git.GitCommit;
import git.GitFile;

import javax.swing.*;
import java.awt.*;

public class DiffView implements IDiffView {
  private Diff diff = new Diff();
  private JTextArea textArea = new JTextArea();

  public DiffView() {
    textArea.setVisible(false);
    textArea.setEnabled(false);
    textArea.setLineWrap(true);
    textArea.setBackground(Color.WHITE);
  }
  /**
   * Opens the difference between the given file and and the previous version of the file.
   *
   * @param fileName the name of the given file.
   */
  public void openDiff(String fileName) {
  }


  public JTextArea openDiffView() {
    return textArea;
  }

  /**
   * Opens the difference between the given file and and the previous version of the file.
   *
   * @param activeCommit
   * @param file
   */
  public void setDiff(GitCommit activeCommit, GitFile file) {
    diff.setDiffCommit(activeCommit, file);
    diff.execute();
    String output = diff.diffGit();
    textArea.setText(output);
    textArea.setDisabledTextColor(Color.BLACK);
    textArea.setVisible(true);
    int width = textArea.getWidth();
    if(width > 0) {
      textArea.setSize(width, Short.MAX_VALUE);
    }
  }

  public void setNotVisible() {
    textArea.setVisible(false);
    textArea.setDisabledTextColor(Color.WHITE);
  }
}
