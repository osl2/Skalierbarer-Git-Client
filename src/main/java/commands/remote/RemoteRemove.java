package commands.remote;

import commands.ICommand;
import commands.ICommandGUI;
import git.GitRemote;

/**
 * This command removes a remote from the list of remote repositories.
 */
public class RemoteRemove extends Remote {

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
