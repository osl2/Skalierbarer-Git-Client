package commands.remote;

import commands.ICommand;
import commands.ICommandGUI;
import git.GitRemote;
import java.net.URL;

/**
 * This command sets the URL parameter of the remote repository to a new URL.
 */
public class RemoteSetURL extends Remote implements ICommand, ICommandGUI {
  private URL remoteURL;
  private GitRemote remote;

  public void setRemoteURL(URL remoteURL) {
    this.remoteURL = remoteURL;
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
