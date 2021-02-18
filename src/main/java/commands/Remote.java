package commands;

import git.GitRemote;

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
   * Method to get the current remote repository.
   *
   * @return The current remote repository
   */
  public GitRemote getRemote(){ return null;}

  /**
   * Method to set tue current remote repository.
   *
   * @param remote The remote repository on which the command should be executed
   *               (getName, getURL, setName, setURL, remove)
   */
  public void setRemote(GitRemote remote){}

  /**
   * Creates with the input the command of the commandline.
   *
   * @return Returns command for Commandline
   */
  public String getCommandLine() {
    return null;
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
    return false;
  }


  /**
   * This method is not abstract, since there is only one Remote button. When this button is clicked, it calls
   * onButtonClicked() on Remote and opens the RemoteView
   */
  public void onButtonClicked(){

  }

  /**
   * TBD
   */
  public enum RemoteSubcommand{ADD, REMOVE, SET_NAME, SET_URL}

}