package commands;

public class Commit implements ICommand, ICommandGUI {
  private String errorMessage;
  private String commandLine;
  private String commandName;
  private String commandDescription;
  private String commitMessage;
  private boolean amend;

  /**
   * This method determines whether the commit should amend the last commit.
   * @param amend True if git commit --amend should be performed, false otherwise.
   */
  public void setAmend(boolean amend){
    this.amend = amend;
  }

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

  public String getErrorMessage() {
    return null;
  }

  /**
   * Method to get the Commandline input that would be necessarry to execute the command.
   *
   * @return Returns a String representation of the corresponding git command
   * to display on the command line
   */
  public String getCommandLine() {
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
