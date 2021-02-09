package commands;

import git.GitBranch;
import git.GitCommit;
import git.GitData;

import java.io.IOException;
import java.util.*;

public class Log implements ICommand {
  private String errorMessage = "";
  private GitData gitData;
  private GitBranch activeBranch;
  private List<GitCommit> commits;

  //-----------------Display Options for log----------------

  public void setSinceDate(Date sinceDate) {
  }

  public void setUntilDate(Date untilDate) {
  }

  public void resetFilter() {

  }

  //------------------------------------------------------



  /**
   * Returns a list of commit messages beginning with the latest commit message of the given branch.
   *
   * @param branch name of the respective branch.
   * @return a list of commit messages.
   */
  public List<GitCommit> getCommits(GitBranch branch) {
    //TODO: Muss vielleicht nochmal überarbeitet werden.
    GitBranch gitBranch;
    if(branch != null) {
      gitBranch = branch;
    } else {
      gitBranch = activeBranch;
    }
    commits = new ArrayList<GitCommit>();
    GitCommit latestCommit = gitBranch.getCommit();
    commits.add(latestCommit);
    while(latestCommit.getParents().length != 0) {
      latestCommit = latestCommit.getParents()[0];
      commits.add(latestCommit);
    }
    return commits;
  }

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   */
  public boolean execute() {
    gitData = new GitData();
    //commits = gitData.getCommits();
    try {
      activeBranch = gitData.getSelectedBranch();
    } catch (IOException e) {
      errorMessage = "Es konnte kein branch Name gefunden werden";
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

}
