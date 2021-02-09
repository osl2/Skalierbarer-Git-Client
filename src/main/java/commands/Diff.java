package commands;

import git.GitCommit;
import git.GitFile;

import java.util.List;

public class Diff implements ICommand {
  private GitCommit activeCommit;
  private GitFile activeFile;
  private String errorMessage;
  private String activeDiff;
  private boolean validDiff = false;

  /**
   * Executes the "git diff" command. Can only be used after @setDiffCommit was called once.
   *
   * @return true, if the command has been executed successfully
   */
  public boolean execute() {
    if(activeCommit == null || activeFile == null) {
      errorMessage = "Es muss ein GitCommit und ein GitFile übergeben werden um den Diff Befehl auszuführen.";
      return false;
    }
    validDiff = true;
    return true;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Sets the commit ID and the file name to compare with the previous one.
   *
   * @param activeCommit the selected commit.
   * @param file the file to compare to his previous version.
   */
  public void setDiffCommit(GitCommit activeCommit, GitFile file) {
    this.activeCommit = activeCommit;
    this.activeFile = file;
    validDiff = false;
  }

  /**
   * Can only be called after setDiffCommit was called.
   * @return the git diff of the given commit and the given file.
   */
  public String diffGit() {
    if(!validDiff) {
      return "";
    }
    String name;
    //name = activeFile.getName();
    return null;
  }

}
