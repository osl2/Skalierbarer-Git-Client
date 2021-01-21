package commands;

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
   *
   * @param commitID the ID of the selected commit.
   * @param fileName the name of the file to compare to his previous version.
   */
  public void setDiffCommit(GitCommit activeCommit, GitFile file) {
  }

  /**
   * Sets the output format of the "git diff" command.
   *
   * @param output constant that provides information on the output format.
   */
  public void showDiff(int output) {
  }

  /**
   * Can only be called after setDiffCommit was called.
   * @return the git diff of the given commit and the given file.
   */
  public String diffGit() {return null;}

  /**
   * Returns a list of all files contained in the given commit.
   *
   * @param commitID the ID of the commit.
   * @return a list of all files contained in the given commit
   */
  public List<String> getFileNames(String commitID) {
    return null;
  }
}
