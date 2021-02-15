package commands;

import controller.GUIController;
import dialogviews.PushDialogView;
import git.GitBranch;
import git.GitFacade;
import git.GitRemote;
import git.exception.GitException;

public class Push implements ICommand, ICommandGUI {
  private GitBranch localBranch;
  private GitRemote remote;
  private GitBranch remoteBranch;
  private boolean setUpstream;

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   */
  public boolean execute() throws GitException{
    boolean success = false;
    GitFacade facade = new GitFacade();
    if (localBranch == null || remote == null){
      throw  new GitException("Kein lokaler Branch oder Remote ausgewählt");
    }
    //remote branch does not yet exist,
    if (remoteBranch == null){
      success = facade.pushOperation(remote, localBranch, setUpstream);
    }
    else{
      //TODO:
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
    return "git push " + remote + " " + localBranch;
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
   * @param remoteBranch
   */
  public void setRemoteBranch(GitBranch remoteBranch){
    this.remoteBranch = remoteBranch;
  }

  /**
   * Chooses if the local repo should follow the remote repo that was configured in setRemote()
   * @param setUpstream True if the local repo should follow the remote repo after the push was executed
   */
  public void setSetUpstream(boolean setUpstream){
    this.setUpstream = setUpstream;
  }
}
