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

  /**
   * Starts mergeprogress for conflict
   */
  public void startMerging(){}

  /**
   * Starts rebaseprogress for conflict
   */
  public void startRebasing(){}

  public String getCommandLine() {
    return "git pull";
  }

  public String getName() {
    return "pull";
  }

  public String getDescription() {
    return "Lädt Änderungen aus einem Online-Repo.";
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

}