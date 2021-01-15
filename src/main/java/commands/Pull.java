package commands;

import git.GitBranch;
import git.GitRemote;


public class Pull implements ICommand, ICommandGUI {
  private GitRemote remote;
  private GitBranch remoteBranch;

  /**
   * Method to get the current remote.
   *
   * @return Returns active repo
   */
  public GitRemote getRemote() {
    return remote;
  }

  /**
   * method to set the current remote.
   *
   * @param remote from which files are to be fetched
   */
  public void setRemote(GitRemote remote) {
    this.remote = remote;
  }

  /**
   * Method to get the currently active remote branch.
   *
   * @return Returns the active remoteBranch
   */
  public GitBranch getRemoteBranch() {
    return remoteBranch;
  }

  /**
   * Method to set the current remote Branch.
   *
   * @param remoteBranch from which files are to be fetched
   */
  public void setRemoteBranch(GitBranch remoteBranch) {
    this.remoteBranch = remoteBranch;
  }

  public String getCommandLine(String userInput) {
    return "git pull";
  }

  public String getName() {
    return "pull";
  }

  public String getDescription() {
    return "Lädt Änderungen aus einem Online-Repo und merged sie";
  }

  @Override
  public void onButtonClicked() {

  }

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   */
  public boolean execute() {
    //not implemented yet
    return false;
  }

  @Override
  public String getErrorMessage() {
    return null;
  }
}