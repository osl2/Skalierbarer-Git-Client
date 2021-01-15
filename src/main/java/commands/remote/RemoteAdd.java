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
public class RemoteAdd extends Remote implements ICommand, ICommandGUI {
  private String remoteName;
  private URL remoteURL;
  private GitRemote remote;


  public void setRemoteName(String remoteName) {
  }

  public void setRemoteUrl(URL remoteURL) {
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

  @Override
  public void onButtonClicked() {

  }
}
