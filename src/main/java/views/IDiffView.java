package views;

import javax.swing.*;

public interface IDiffView extends JPanel {

  /**
   * Opens the view of the difference between a selected file and the
   * previous version of the file.
   */
  public JPanel openDiffView();

  /**
   * Opens the difference between the given file and and the previous version of the file.
   *
   * @param fileName the name of the given file.
   */
  public void setDiff(GitCommit activeCommit, GitFile file);
}
