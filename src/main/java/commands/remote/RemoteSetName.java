package commands.remote;

import commands.ICommand;
import commands.ICommandGUI;
import git.GitRemote;

/**
 * This command changes the name of the remote repository.
 */
public class RemoteSetName extends Remote implements ICommand, ICommandGUI {
  private String remoteName;
  private GitRemote remote;

  public void setRemoteName(String remoteName) {
    this.remoteName = remoteName;
  }

  @Override
  public GitRemote getRemote() {
    return null;
  }

  @Override
  public void setRemote(GitRemote remote) {

  }

  @Override
  public boolean execute() {
    return false;
  }

  @Override
  public String getErrorMessage() {
    return null;
  }

  @Override
  public String getCommandLine(String userInput) {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public void onButtonClicked() {

  }
}
