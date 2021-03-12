package commands;

import controller.GUIController;
import dialogviews.PushDialogView;
import git.*;
import git.exception.GitException;

import java.util.List;

/**
 * Represents the git push command. In order to execute this command you
 * have to pass a local {@link GitBranch} and a {@link GitRemote}.
 */
public class Push implements ICommand, ICommandGUI {
  private GitBranch localBranch;
  private GitRemote remote;
  private String remoteBranchName;
  private List<GitBranch> remoteBranchList;

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   */
  @Override
  public boolean execute() {
    boolean success;

    //check wether local branch and remote have been set
    if (localBranch == null) {
      GUIController.getInstance().errorHandler("Kein lokaler Branch ausgewählt");
      return false;
    } else if (remote == null) {
      GUIController.getInstance().errorHandler("Kein Remote ausgewählt");
      return false;
    }
    //remote branch does not yet exist, git will automatically create one with the same name as the local branch
    if (remoteBranchName == null) {
      remoteBranchName = localBranch.getName();
      success = tryExecute();
    }

    //remote branch already exists
    else {
      if (!getRemoteBranches()) {
        return false;
      }
      for (GitBranch branch : remoteBranchList) {
        if (branch.getName().compareTo(remoteBranchName) == 0) {
          GUIController.getInstance().errorHandler("Es existiert bereits ein anderer Branch mit diesem Namen");
          return false;
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
  @Override
  public String getCommandLine() {
    return "git push " + remote.getName() + " " + localBranch.getName() +
            (remoteBranchName != null && remoteBranchName.compareTo(localBranch.getName()) != 0 ? ":" + remoteBranchName : "");
  }

  /**
   * Method to get the name of the command, that could be displaied in the GUI.
   *
   * @return The name of the command
   */
  @Override
  public String getName() {
    return "Push";
  }

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a Sting
   */
  @Override
  public String getDescription() {
    return "Lädt die lokalen Einbuchungen aus dem aktuellen Branch in das Online-Verzeichnis hoch";
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public void onButtonClicked() {
    GUIController c = GUIController.getInstance();
    c.openDialog(new PushDialogView());
  }

  /**
   * Sets the local branch that should be pushed to the online repo
   * @param localBranch The local branch whose commits should be pushed
   */
  public void setLocalBranch(GitBranch localBranch) {
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
   *
   * @param remoteBranchName the name of the new RemoteBranch
   */
  public void setRemoteBranchName(String remoteBranchName) {
    this.remoteBranchName = remoteBranchName;
  }

  /*
  Tries to execute the command. If authentification is required, the CredentialProvider will be activated and the
  command execution is retried.
   */
  private boolean tryExecute() {
    GitFacade gitFacade = new GitFacade();

    try {
      return gitFacade.pushOperation(remote, localBranch, remoteBranchName);
    } catch (GitException e) {
      CredentialProviderHolder.getInstance().changeProvider(true, remote.getName());
      if (CredentialProviderHolder.getInstance().isActive()) {
        return tryExecute();
      } else {
        CredentialProviderHolder.getInstance().setActive(true);
        return false;
      }
    }
  }

  /*
   * Returns all branches in the given remote. Activates the CredentialProviderHolder if authentification is required.
   */
  private boolean getRemoteBranches() {
    GitData git = new GitData();
    try {
      remoteBranchList = git.getBranches(remote);
      return true;
    } catch (GitException e) {
      CredentialProviderHolder.getInstance().changeProvider(true, remote.getName());
      if (CredentialProviderHolder.getInstance().isActive()) {
        return getRemoteBranches();
      } else {
        CredentialProviderHolder.getInstance().setActive(true);
        return false;
      }
    }
  }
}

