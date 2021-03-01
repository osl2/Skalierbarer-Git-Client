package commands;

import controller.GUIController;
import dialogviews.PushDialogView;
import git.*;
import git.exception.GitException;

import java.util.List;

/**
 * This class represents the git push command. In order to execute this command you
 * have to pass a local {@link GitBranch} and a {@link GitRemote}.
 */
public class Push implements ICommand, ICommandGUI {
  private GitBranch localBranch;
  private GitRemote remote;
  private String remoteBranch;
  private List<GitBranch> branchList;

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   */
  public boolean execute(){
    boolean success;

    //check wether local branch and remote have been set
    if (localBranch == null){
      GUIController.getInstance().errorHandler("Kein lokaler Branch ausgewählt");
      return false;
    }
    else if (remote == null){
      GUIController.getInstance().errorHandler("Kein Remote ausgewählt");
      return false;
    }
    //remote branch does not yet exist, git will automatically create one with the same name as the local branch
    if (remoteBranch == null){
      remoteBranch = localBranch.getName();
      success = tryExecute();
    }
    //remote branch already exists
    else{
      if (!getBanches()){
        return false;
      }
      for (GitBranch branch : branchList){
        if (branch.getName().compareTo(remoteBranch) == 0){
          GUIController.getInstance().errorHandler("Es exitiert bereits ein anderer Branch mit diesem namen");
        }
      }
      success = tryExecute();
    }
    return success;
  }

  /**
   * Method to get the Commandline input that would be necessarry to execute the command.
   *
   * @return Returns a String representation of the corresponding git command to display
   * on the command line
   */
  public String getCommandLine() {
    return "git push " + remote.getName() + " " + localBranch.getName();
  }

  /**
   * Method to get the name of the command, that could be displaied in the GUI.
   *
   * @return The name of the command
   */
  public String getName() {
    return "Push";
  }

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a Sting
   */
  public String getDescription() {
    return "Lädt die lokalen Einbuchungen aus dem aktuellen Branch in das Online-Verzeichnis hoch";
  }
  /**
   * {@inheritDoc}
   */
  public void onButtonClicked() {
    GUIController c = GUIController.getInstance();
    c.openDialog(new PushDialogView());
  }

  /**
   * Sets the local branch that should be pushed to the online repo
   * @param localBranch The local branch whose commits should be pushed
   */
  public void setLocalBranch(GitBranch localBranch){
    this.localBranch = localBranch;
  }

  /**
   * Sets the remote repo the local commits should be pushed to
   * @param remote The online repo (must have been configured before)
   */
  public void setRemote(GitRemote remote){
    this.remote = remote;
  }

  /**
   * Sets the remote branch the local commits should be pushed to. If the remote branch does not exist,
   * a new upstream branch will be created
   * @param remoteBranch the name of the new RemoteBranch
   */
  public void setRemoteBranch(String remoteBranch){
    this.remoteBranch = remoteBranch;
  }

  private boolean tryExecute(){
    GitFacade gitFacade = new GitFacade();
    try {
      gitFacade.pushOperation(remote, localBranch, remoteBranch);
      return true;
    } catch (GitException e) {
      CredentialProviderHolder.getInstance().changeProvider(true, remote.getName());
      if (CredentialProviderHolder.getInstance().isActive()){
        return tryExecute();
      }
      else {
        CredentialProviderHolder.getInstance().setActive(true);
        return false;
      }
    }
  }
  private boolean getBanches(){
    GitData git = new GitData();
    try {
      branchList = git.getBranches(remote);
      return true;
    } catch (GitException e){
      CredentialProviderHolder.getInstance().changeProvider(true, remote.getName());
      if (CredentialProviderHolder.getInstance().isActive()){
        return getBanches();
      }
      else {
        CredentialProviderHolder.getInstance().setActive(true);
        return false;
      }
    }
  }
}

