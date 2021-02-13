package commands;

import git.GitRemote;

import java.util.List;

public class Fetch implements ICommand, ICommandGUI {
  private String errorMessage;
  private String commandLine;
  private String commandName;
  private String commandDescription;

  /**
   * Executes the "git fetch" command. Can only be used after setRemotes was called once.
   *
   * @return True, if it is successfully executed false if not
   */
  public boolean execute() {
    //not implemented yet
    return false;
  }

  public String getErrorMessage() {
    return null;
  }

  /**
   * Returns a list containing all remote names.
   *
   * @return a list with with remote names.
   */
  public List<GitRemote> getRemotes() {
    return null;
  }

  /**
   * Sets the selected remotes to execute the "git fetch" command.
   *
   * @param names the names of the selected remotes.
   */
  public void setRemotes(List<GitRemote> names) {
  }

  /**
   * Method to get the Commandline input that would be necessarry to execute the command.
   *
   * @return Returns a String representation of the corresponding
   * git command to display on the command line
   */
  public String getCommandLine() {
    return null;
  }

  /**
   * Method to get the name of the command, that could be displayed in the GUI.
   *
   * @return The name of the command
   */
  public String getName() {
    return null;
  }

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a Sting
   */
  public String getDescription() {
    return null;
  }

  public void onButtonClicked() {

  }
}
