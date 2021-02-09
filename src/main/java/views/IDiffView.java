package views;

import git.GitCommit;
import git.GitFile;

import javax.swing.*;

public interface IDiffView {

  /**
   * Opens the view of the difference between a selected file and the
   * previous version of the file.
   */
  JTextArea openDiffView();

  /**
   * Opens the difference between the given file and and the previous version of the file.
   *
   * @param fileName the name of the given file.
   */
  void setDiff(GitCommit activeCommit, GitFile file);
}
