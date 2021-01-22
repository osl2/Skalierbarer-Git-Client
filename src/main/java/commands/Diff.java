package commands;

import git.GitCommit;
import git.GitFile;

import java.util.List;

public class Diff implements ICommand {
  private String errorMessage;

  /**
   * Executes the "git diff" command. Can only be used after @setDiffCommit was called once.
   *
   * @return true, if the command has been executed successfully
   */
  public boolean execute() {
    //not implemeneted yet
    return false;
  }

  public String getErrorMessage() {
    return null;
  }

  /**
   * Sets the commit ID and the file name to compare with the previous one.
   * TODO: Fix params
   *
   * @param commitID the ID of the selected commit.
   * @param fileName the name of the file to compare to his previous version.
   */
  public void setDiffCommit(GitCommit activeCommit, GitFile file) {
  }

  /**
   * Can only be called after setDiffCommit was called.
   * @return the git diff of the given commit and the given file.
   */
  public String diffGit() {return null;}

}
