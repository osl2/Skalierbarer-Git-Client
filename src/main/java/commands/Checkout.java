package commands;

import git.GitBranch;
import git.GitCommit;
import java.util.List;

public class Checkout implements ICommand, ICommandGUI {

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   */
  @Override
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
   * @param userInput Input wich the user has to make individually for executing the command
   * @return Returns a String representation of the corresponding git command to
   *     display on the command line
   */
  @Override
  public String getCommandLine(String userInput) {
    return null;
  }

  /**
   * Method to get the name of the command, that could be displaied in the GUI.
   *
   * @return The name of the command
   */
  @Override
  public String getName() {
    return null;
  }

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a String
   */
  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public void onButtonClicked() {

  }

  public List<GitBranch> getTree() {
    //not implemented yet
    return null;
  }

  public void checkout(GitCommit commit) {
    throw new AssertionError("not implemented");
  }

  public void checkout(GitBranch branch) {
    throw new AssertionError("not implemented");
  }

}