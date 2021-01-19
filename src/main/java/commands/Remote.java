package commands;

import commands.ICommand;
import commands.ICommandGUI;
import git.GitRemote;

import java.net.URL;

public class Remote implements ICommand, ICommandGUI {
  private GitRemote remote;
  private String commandLine;
  private String name;
  private URL url;

  public void setFirstRemoteSubcommand(RemoteSubcommand firstRemoteSubcommand) {
    this.firstRemoteSubcommand = firstRemoteSubcommand;
  }

  public void setSecRemoteSubcommand(RemoteSubcommand secRemoteSubcommand) {
    this.secRemoteSubcommand = secRemoteSubcommand;
  }

  private RemoteSubcommand firstRemoteSubcommand;
  private RemoteSubcommand secRemoteSubcommand = null;
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
   * @param userInput Input off the user
   * @return Returns command for Commandline
   */
  public String getCommandLine(String userInput){return null;}

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
   *
   * @return Returns an error message when execute() fails
   */
  public String getErrorMessage(){return null;}

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