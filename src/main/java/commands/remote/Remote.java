package commands.remote;

import commands.ICommand;
import commands.ICommandGUI;
import git.GitRemote;

public abstract class Remote implements ICommand, ICommandGUI {

  /**
   * Method to get the current remote repository.
   *
   * @return The current remote repository
   */
  public abstract GitRemote getRemote();

  /**
   * Method to set tue current remote repository.
   *
   * @param remote The remote repository on which the command should be executed
   *               (getName, getURL, setName, setURL, remove)
   */
  public abstract void setRemote(GitRemote remote);

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


}