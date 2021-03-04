package commands;


import com.fasterxml.jackson.annotation.JsonCreator;
import git.GitBranch;

/**
 * This class represents the git rebase command. In order to execute you have
 * to pass two branches.
 */
public class Rebase implements ICommand, ICommandGUI {

  private final GitBranch branchA;
  private final GitBranch branchB;

  /**
   * Creates a new instance an sets the two branches to rebase.
   * @param branchA the {@link GitBranch} to rebase onto.
   * @param branchB the {@link GitBranch} which gets rebased.
   */
  public Rebase(GitBranch branchA, GitBranch branchB) {
    this.branchA = branchA;
    this.branchB = branchB;
  }

  /**
   * Used by Jackson to create object for Level.
   */
  @JsonCreator
  public Rebase() {
    /* Used by Jackson to create object for Level */
    this.branchA = null;
    this.branchB = null;
  }

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   */
  @Override
  public boolean execute() {
    return false;
  }


  /**
   * Method to get the Commandline input that would be necessarry to execute the command.
   *
   * @return Returns a String representation of the corresponding
   * git command to display on the command line
   */
  @Override
  public String getCommandLine() {
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
   * @return description as a Sting
   */
  @Override
  public String getDescription() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onButtonClicked() {
  }
}

