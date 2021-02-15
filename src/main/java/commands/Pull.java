package commands;

import controller.GUIController;
import dialogviews.PullConflictDialogView;
import git.GitBranch;
import git.GitData;
import git.GitFacade;
import git.GitRemote;
import git.exception.GitException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
  public void startMerging() {}

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
    if(remote == null || remoteBranch == null) {
      GUIController.getInstance().errorHandler("Es muss ein remote und ein branch auf dem remote übergeben werden.");
      return false;
    }
    GitFacade facade = new GitFacade();
    ArrayList<GitRemote> remoteList = new ArrayList<GitRemote>();
    remoteList.add(remote);
    boolean success = facade.fetchRemotes(remoteList);
    if(!success) {
      GUIController.getInstance().errorHandler("Es konnte kein remote gefunden werden.");
      return false;
    }
    GitData data = new GitData();
    GitBranch dest;
    List<GitBranch> allBranches;
    try {
      dest = data.getSelectedBranch();
      allBranches = data.getBranches();
    } catch (IOException | GitException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    }
    GitBranch src = null;
    for(int i = 0; i < allBranches.size(); i++) {
      if(allBranches.get(i).getName().compareTo(remote.getName() + "/" + remoteBranch.getName()) == 0) {
        src = allBranches.get(i);
        break;
      }
    }
    GUIController.getInstance().openDialog(new PullConflictDialogView(src, dest));
    return true;
  }
}