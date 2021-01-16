package commands;

public class Commit implements ICommand, ICommandGUI {

  /**
   * This method sets the commit message of the next commit.
   *
   * @param commitMessage the message of the next commit.
   */
  public void setCommitMessage(String commitMessage) {

  }

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   */
  public boolean execute() {
    return false;
  }

  @Override
  public String getErrorMessage() {
    return null;
  }

  /**
   * Method to get the Commandline input that would be necessarry to execute the command.
   *
   * @param userInput Input that the user needs to make additionally to the
   *                  standard output of git commit
   * @return Returns a String representation of the corresponding git command
   *     to display on the command line
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

  @Override
  public void onButtonClicked() {

  }
}