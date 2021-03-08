package commands;

import controller.GUIController;
import git.GitCommit;
import git.GitFile;
import git.exception.GitException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class represents the git diff command. In order to execute the command you have to pass
 * a {@link GitCommit} and a {@link GitFile} or just a {@link GitFile}. This Command creates the diff
 * between a given Commit and the previous one.
 */
public class Diff implements ICommand {
  private GitCommit activeCommit;
  private GitFile activeFile;
  private String activeDiff;
  private boolean validDiff = false;
  private boolean commit = false;

  /**
   * Executes the "git diff" command. Can only be used after @setDiffCommit
   * or @setDiffFile was called once.
   *
   * @return true, if the command has been executed successfully
   */
  @Override
  public boolean execute() {
    if((commit && activeCommit == null) || activeFile == null) {
      GUIController.getInstance().errorHandler( "Es muss ein GitCommit und ein GitFile " +
              "übergeben werden um den Diff Befehl auszuführen.");
      return false;
    }
    if(!commit) {
      try {
        activeDiff = GitCommit.getDiff(activeFile);
      } catch (IOException | GitException e) {
        GUIController.getInstance().errorHandler(e);
        return false;
      }
    } else {
      try {
        if (activeCommit.getParents().length != 0) {
          activeDiff = activeCommit.getDiff(activeCommit.getParents()[0], activeFile);
          // If this Commit is the first one in the repository.
        } else {
          activeDiff = activeCommit.getDiff(null, activeFile);
        }
      } catch (IOException e) {
        GUIController.getInstance().errorHandler(e);
        return false;
      }
    }
    validDiff = true;
    return true;
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
    commit = true;
  }

  /**
   * Sets the Diff command to return the deference between the given file and the
   * working directory.
   * @param file the File to compare to the working directory.
   */
  public void setDiffFile(GitFile file) {
    this.activeFile = file;
    validDiff = false;
    commit = false;
  }

  /**
   * Can only be called after setDiffCommit was called.
   * @return the git diff of the given commit and the given file.
   */
  public String[] diffGit() {
    if (!validDiff) {
      return new String[]{};
    }
    ArrayList<String> lines = new ArrayList<>();
    activeDiff.lines().forEach(lines::add);
    if (lines.size() < 4) {
      return new String[]{};
    }
    // Cut of the upper part of the diff and begin with the changed lines count.
    int startLine = 5;
    if (lines.get(4).substring(0, 2).compareTo("@@") == 0) {
      startLine = 4;
    }
    String[] output = new String[lines.size() - startLine];
    for (int i = startLine; i < lines.size(); i++) {
      output[i - startLine] = lines.get(i);
    }
    return output;
  }
}
