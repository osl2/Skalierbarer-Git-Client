package commands;


import git.GitFile;
import java.util.List;

public class Add implements ICommand, ICommandGUI {
  private String errorMessage;
  private String commandLine;
  private String commandName;
  private String commandDescription;

  /**
   * Performs git add.
   *
   * @return true, if the command has been executed successfully
   */
  public boolean execute() {
    return false;
  }


  /**
   * The method returns the error message in case the command has not been executed successfully.
   *
   * @return empty String if the command was executed successfully, the error String otherwise
   */
  public String getErrorMessage() {
    return null;
  }

  /**
   * This method passes a list of blobs (changes in files) to add them to the staging area.
   * The blobs need to be passed before execute() is invoked. Otherwise, execution will fail.
   *
   * @param blobs A List of changed files. The list must contain at least one item.
   *              TODO: Discuss whether a list of Strings (file paths) would be more suitable
   */
  public void setBlobs(List<GitFile> blobs) {
  }

  /**
   * The method returns a description of the git add command.
   *
   * @return Returns a short description of the command,
   *     which can be displayed to the user if necessary
   */
  public String getCommandDescription() {
    return "Fügt Änderungen zur Staging-Area hinzu";
  }

  /**
   * Method to get the Commandline input that would be
   *     necessarry to execute the command.
   *
   * @return Returns a String representation of the corresponding
   *     git command to display on the command line
   */
  public String getCommandLine(String userInput) {
    return null;
  }

  /**
   * Method to get the name of the command, that could be displaied in the GUI.
   *
   * @return The name of the command
   */
  public String getName() {
    return null;
  }

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a String
   */
  public String getDescription() {
    return null;
  }

  public void onButtonClicked() {

  }

}
