package commands.remote;

import commands.ICommand;
import commands.ICommandGUI;
import git.GitRemote;

public class RemoteGetName extends Remote implements ICommand, ICommandGUI {
  private GitRemote remote;
  private String returnName;

  public String getReturnName() {
    return returnName;
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
