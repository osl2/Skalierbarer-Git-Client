package commands;

import git.GitBranch;
import git.GitRemote;

public class Push implements ICommand, ICommandGUI {
  private GitBranch branch;
  private GitRemote remote;
  private boolean following;

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   */
  public boolean execute() {
    return false;
  }

  public String getErrorMessage() {
    return null;
  }

  /**
   * Method to get the Commandline input that would be necessarry to execute the command.
   *
   * @param userInput The input that the user needs to make additionally to
   *                  the standard output of git commit
   * @return Returns a String representation of the corresponding git command to display
   *     on the command line
   */
  public String getCommandLine(String userInput) {
    return null;
  }

  /**
   * Method to get the name of the command, that could be displaied in the GUI.
   *
   * @return The name of the command
   */
  public String getName() {
    return null;
  }

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a Sting
   */
  public String getDescription() {
    return null;
  }

  public void onButtonClicked() {

  }

  /**
   * Sets the local branch that should be pushed to the online repo
   * @param branch The local branch whose commits should be pushed
   */
  public void setBranch(GitBranch branch){}

  /**
   * Sets the remote repo the local commits should be pushed to
   * @param remote The online repo (must have been configured before)
   */
  public void setRemote(GitRemote remote){}

  /**
   * Chooses if the local repo should follow the remote repo that was configured in setRemote()
   * @param following True if the local repo should follow the remote repo after the push was executed
   */
  public void setFollowing(boolean following){}
}
