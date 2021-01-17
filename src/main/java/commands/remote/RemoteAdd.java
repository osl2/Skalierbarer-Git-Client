package commands.remote;

import commands.ICommand;
import commands.ICommandGUI;
import git.GitRemote;
import java.net.URL;

/**
 * This command adds a new remote repository. Before execution, the user needs
 * to define a name and the URL.
 * Both parameters can be configured later.
 *
 * @see commands.remote
 */
public class RemoteAdd extends Remote {
  private String errorMessage;
  private String commandLine;
  private String commandName;
  private String commandDescription;

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
    return "git remote add";
  }

  @Override
  public String getName() {
    return "remote add";
  }

  @Override
  public String getDescription() {
    return null;
  }

  /**
   * Should do nothing since there is no dedicated Button for Adding Remotes
   */
  @Override
  public void onButtonClicked() {

  }
}
