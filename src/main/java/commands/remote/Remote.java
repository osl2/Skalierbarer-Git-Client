package commands.remote;

import commands.ICommand;
import commands.ICommandGUI;
import git.GitRemote;

public abstract class Remote implements ICommand, ICommandGUI {
  protected GitRemote remote;
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
   * Method to create new remote.
   *
   * @return true, if the command has been executed successfully
   */
  public abstract boolean execute();

  /**
   * Creates with the input the command of the commandline.
   *
   * @param userInput Input off the user
   * @return Returns command for Commandline
   */
  public abstract String getCommandLine(String userInput);

  /**
   * Method to get the name of the command.
   *
   * @return Returns the name of the command
   */
  public abstract String getName();

  /**
   * Method to get a description of the command.
   *
   * @return Returns a Description of what the command is doing
   */
  public abstract String getDescription();

  /**
   *
   * @return Returns an error message when execute() fails
   */
  public abstract String getErrorMessage();

  /**
   * This method is not abstract, since there is only one Remote button. When this button is clicked, it calls
   * onButtonClicked() on Remote and opens the RemoteView
   */
  public void onButtonClicked(){

  }


}