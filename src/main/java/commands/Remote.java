package commands;

import controller.GUIController;
import git.GitData;
import git.GitFacade;
import git.GitRemote;
import git.exception.GitException;
import views.RemoteView;

import java.util.List;

/**
 * This class represents the git remote command. It uses {@link RemoteSubcommand} to
 * indicate which command should be executed.
 */
public class Remote implements ICommand, ICommandGUI {
  private GitRemote gitRemote;
  private String remoteName;
  private String url;

  /**
   * Sets the subcommand which indicates which remote command should be executed
   * @param remoteSubcommand the remote command to execute.
   */
  public void setRemoteSubcommand(RemoteSubcommand remoteSubcommand) {
    this.remoteSubcommand = remoteSubcommand;
  }


  private RemoteSubcommand remoteSubcommand;
  /**
   * Method to set tue current remote repository.
   *
   * @param gitRemote The remote repository on which the command should be executed
   *               (getName, getURL, setName, setURL, remove)
   */
  public void setRemote(GitRemote gitRemote){
    this.gitRemote = gitRemote;
  }

  /**
   * Creates with the input the command of the commandline.
   *
   * @return Returns command for Commandline
   */
  @Override
  public String getCommandLine() {
    String ret ;
    switch (remoteSubcommand){
      case ADD: ret =  "git remote add " + remoteName + " " + url;
      break;
      case SET_URL: ret = "git remote set-url " + gitRemote.getName() + " " + url;
      break;
      case REMOVE: ret = "git remote rm " + gitRemote.getName();
      break;
      default: ret = null;
    }
    return ret;
  }

  /**
   * Method to get the name of the command.
   *
   * @return Returns the name of the command
   */
  @Override
  public String getName(){return "Remote";}

  /**
   * Method to get a description of the command.
   *
   * @return Returns a Description of what the command is doing
   */
  @Override
  public String getDescription(){return "Möglichkeit die Onlinerepositories hinzuzufügen und zu verwalten";}

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute() {
    switch (remoteSubcommand) {
      case SET_URL:
        try {
          return gitRemote.setUrl(url);
        } catch (GitException e) {
          GUIController.getInstance().errorHandler(e);
          return false;
        }
      case ADD:
        GitFacade gitFacade = new GitFacade();
        GitData gitData = new GitData();
        List<GitRemote> remoteList = gitData.getRemotes();
        for (GitRemote gitRemoteNew : remoteList) {
          if (gitRemoteNew.getName().compareTo(remoteName) == 0) {
            GUIController.getInstance().errorHandler("Ein Remote mit diesem namen existiert bereits");
            return false;
          }
        }
        try {
          return gitFacade.remoteAddOperation(remoteName, url);
        } catch (GitException e) {
          GUIController.getInstance().errorHandler(e);
          return false;
        }

      case REMOVE:
        try {
          gitRemote.remove();
          return true;
        } catch (GitException e) {
          GUIController.getInstance().errorHandler(e);
          return false;
        }

      default: return false;
    }
  }


  /**
   * This method is not abstract, since there is only one Remote button. When this button is clicked, it calls
   * onButtonClicked() on Remote and opens the RemoteView
   */
  @Override
  public void onButtonClicked(){
    GUIController.getInstance().openView(new RemoteView());
  }

  /**
   * Sets the name of the remote.
   * @param remoteName name of the remote.
   */
  public void setRemoteName(String remoteName) {
    this.remoteName = remoteName;
  }

  /**
   * Sets the url of the remote.
   * @param url the url of the remote.
   */
  public void setUrl(String url) {
    this.url = url;
  }


  /**
   * This subcommands indicate which git remote operation is needed.
   */
  public enum RemoteSubcommand{ADD, REMOVE, SET_URL, INACTIVE}

}