package commands;

import controller.GUIController;
import git.GitCommit;
import git.GitFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

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
      GUIController.getInstance().errorHandler( "Es muss ein GitCommit und ein GitFile " +
              "übergeben werden um den Diff Befehl auszuführen.");
      return false;
    }
    try {
      if(activeCommit.getParents().length != 0) {
        activeDiff = activeCommit.getDiff(activeCommit.getParents()[0], activeFile);
      } else {
        activeDiff = activeCommit.getDiff(null, activeFile);
      }
    } catch (IOException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    }
    validDiff = true;
    return true;
  }

  public String getErrorMessage() {
    return null;
  }

  /**
   * Sets the commit ID and the file name to compare with the previous one.
   *
   * @param activeCommit the selected commit.
   * @param file the file to compare to his previous version.
   */
  public void setDiffCommit(GitCommit activeCommit, GitFile file) {
    this.activeCommit = activeCommit;
    validDiff = false;
    this.activeFile = file;
  }

  /**
   * Can only be called after setDiffCommit was called.
   * @return the git diff of the given commit and the given file.
   */
  public String[] diffGit() {
    if(!validDiff) {
      String[] out = new String[]{""};
      return out;
    }
    ArrayList<String> lines = new ArrayList<String>();
    activeDiff.lines().forEach(lines::add);
    // Cut of the upper part of the diff and begin with the changed lines count.
    int startLine = 5;
    if(lines.get(4).substring(0, 2).compareTo("@@") == 0) {
      startLine = 4;
    }
    String[] output = new String[lines.size() - startLine];
    for(int i = startLine; i < lines.size(); i++) {
      output[i - startLine] = lines.get(i);
    }
    return output;
  }
}
