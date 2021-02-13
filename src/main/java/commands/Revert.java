package commands;

import git.GitBranch;
import git.GitCommit;

public class Revert implements ICommand, ICommandGUI {
  private GitBranch chosenBranch;
  private GitCommit chosenCommit;

  /**
   * Method to revert back to a chosen commit.
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
   * Creates with the input the command of the commandline.
   *
   * @return Returns command for Commandline
   */
  public String getCommandLine() {
    return null;
  }

  /**
   * Method to get the name of the revert command.
   *
   * @return Returns the name of the command
   */
  public String getName() {
    return null;
  }

  /**
   * Method to get a description of the revert command.
   *
   * @return Returns a Description of what the command is doing
   */
  public String getDescription() {
    return null;
  }

  public void onButtonClicked() {

  }

  /**
   * Method to get the currently chosen branch.
   *
   * @return Returns the current chosen branch
   */
  public GitBranch getChosenBranch() {
    return chosenBranch;
  }

  /**
   * Sets the variable chosen branch to the new branch.
   *
   * @param chosenBranch new value of the variable chosenBranch
   */
  public void setChosenBranch(GitBranch chosenBranch) {
    this.chosenBranch = chosenBranch;
  }

  /**
   * Method to get the currently chosen commit.
   *
   * @return Returns the current chosen commit
   */
  public GitCommit getChosenCommit() {
    return chosenCommit;
  }

  /**
   * Sets the variable chosenCommit to the new commit.
   *
   * @param chosenCommit new value of the variable chosenCommit
   */
  public void setChosenCommit(GitCommit chosenCommit) {
    this.chosenCommit = chosenCommit;
  }
}