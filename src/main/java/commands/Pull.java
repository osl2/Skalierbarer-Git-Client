package commands;

import controller.GUIController;
import dialogviews.PullConflictDialogView;
import dialogviews.PullDialogView;
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
  private String commandLine;

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
    return commandLine;
  }

  public String getName() {
    return "pull";
  }

  public String getDescription() {
    return "Lädt Änderungen aus einem Online-Repo.";
  }

  @Override
  public void onButtonClicked() {
    GUIController.getInstance().openDialog(new PullDialogView());
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
    try {
      facade.fetchRemotes(remoteList);
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    }
    GitData data = new GitData();
    GitBranch dest = null;
    List<GitBranch> allBranches;
    try {
      allBranches = data.getBranches();
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    }
    GitBranch master = null;
    GitBranch src = null;
    for(int i = 0; i < allBranches.size(); i++) {
      // Find fetched branch.
      if(allBranches.get(i).getName().compareTo(remote.getName() + "/" + remoteBranch.getName()) == 0) {
        src = allBranches.get(i);
      }
      // Checks if the fetched branch exists locally.
      if(allBranches.get(i).getName().compareTo(remoteBranch.getName()) == 0) {
        dest = allBranches.get(i);
      }
      // Find master branch if this branch was fetched the first time.
      if(allBranches.get(i).getName().compareTo("master") == 0) {
        master = allBranches.get(i);
      }
    }
    // If fetched branch do not exist locally create new local branch.
    // The new created branch is based on the head commit of the master branch.
    if(dest == null) {
      try {
        facade.branchOperation(master.getCommit(), remoteBranch.getName());
      } catch (GitException e) {
        GUIController.getInstance().errorHandler(e);
        return false;
      }
    }
    GUIController.getInstance().closeDialogView();
    commandLine = remote.getName() + remoteBranch.getName();
    GUIController.getInstance().openDialog(new PullConflictDialogView(src, dest, getCommandLine()));
    return true;
  }
}