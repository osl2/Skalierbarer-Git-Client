package commands.remote;

import commands.ICommand;
import commands.ICommandGUI;
import git.GitRemote;
import java.net.URL;

public class RemoteGetURL extends Remote implements ICommand, ICommandGUI {
  private GitRemote remote;
  private URL returnURL;

  public URL getReturnURL() {
    return returnURL;
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
