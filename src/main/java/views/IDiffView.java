package views;

import git.GitCommit;
import git.GitFile;

import javax.swing.*;

public interface IDiffView {

  /**
   * Opens the view of the difference between a selected file and the
   * previous version of the file.
   *
   * @return the TextPanel containing the Diff
   */
  JTextPane openDiffView();

  /**
   * Opens the difference between the given commit and the previous one.
   * Only the git diff of the given File will be displayed.
   *
   * @param activeCommit is the commit to compare to its previous version.
   * @param file is a the File of the given commit.
   */
  void setDiff(GitCommit activeCommit, GitFile file);

  /**
   * Opens the difference between the given File and the working directory.
   * @param file a File which changed since the latest commit.
   */
  void setDiff(GitFile file);
}
