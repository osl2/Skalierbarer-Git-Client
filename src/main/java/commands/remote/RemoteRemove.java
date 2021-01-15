package commands.remote;

import commands.ICommand;
import commands.ICommandGUI;
import git.GitRemote;

/**
 * This command removes a remote from the list of remote repositories.
 */
public class RemoteRemove extends Remote implements ICommand, ICommandGUI {
  private String remoteName;
  private GitRemote remote;

  /**
   * This method specifies the name of the remote that should be removed from the list.
   * If the remote with the
   * given name is not in the list, execute() will return false.
   *
   * @param name The name of the remote that should be removed.
   * @see commands.remote.RemoteAdd
   */
  public void setRemoteName(String name) {
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
