package commands;

import controller.GUIController;
import git.GitFacade;
import git.GitRemote;
import git.exception.GitException;

import java.net.URL;

public class Remote implements ICommand, ICommandGUI {
  private GitRemote remote;
  private String subCommandName;
  private String remoteName;
  private URL url;

  public void setRemoteSubcommand(RemoteSubcommand remoteSubcommand) {
    this.remoteSubcommand = remoteSubcommand;
  }


  private RemoteSubcommand remoteSubcommand;
  /**
   * Method to set tue current remote repository.
   *
   * @param remote The remote repository on which the command should be executed
   *               (getName, getURL, setName, setURL, remove)
   */
  public void setRemote(GitRemote remote){
    this.remote = remote;
  }

  /**
   * Creates with the input the command of the commandline.
   *
   * @return Returns command for Commandline
   */
  public String getCommandLine() {
    String ret = "";
    switch (remoteSubcommand){
      case ADD: ret =  "git remote add " + remoteName + " " + url.toString();
      break;
      case SET_URL: ret = "git remote set-url " + remote.getName() + " " + url.toString();
      break;
      case REMOVE: ret = "git remote rm " + remote.getName();
      break;
    }
    return ret;
  }

  /**
   * Method to get the name of the command.
   *
   * @return Returns the name of the command
   */
  public String getName(){return "remote";}

  /**
   * Method to get a description of the command.
   *
   * @return Returns a Description of what the command is doing
   */
  public String getDescription(){return null;}

  public boolean execute() {
    switch (remoteSubcommand) {
      case SET_URL:
        try {
          return remote.setUrlGit(url);
        } catch (GitException e) {
          GUIController.getInstance().errorHandler(e);
          return false;
        }
      case ADD:
        GitFacade gitFacade = new GitFacade();
        try {
          return gitFacade.remoteAddOperation(remoteName, url);
        } catch (GitException e) {
          GUIController.getInstance().errorHandler(e);
          return false;
        }

      case REMOVE:
        try {
          remote.remove();
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
  public void onButtonClicked(){

  }

  public void setRemoteName(String remoteName) {
    this.remoteName = remoteName;
  }

  public void setUrl(URL url) {
    this.url = url;
  }

  public RemoteSubcommand getRemoteSubcommand() {
    return remoteSubcommand;
  }

  /**
   * TBD
   */
  public enum RemoteSubcommand{ADD, REMOVE, SET_URL, INACTIVE}

}