package commands;


import com.fasterxml.jackson.annotation.JsonCreator;
import git.GitBranch;

public class Rebase implements ICommand, ICommandGUI {

  private final GitBranch branchA;
  private final GitBranch branchB;

  public Rebase(GitBranch branchA, GitBranch branchB) {
    this.branchA = branchA;
    this.branchB = branchB;
  }

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
  public boolean execute() {
    return false;
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
   * @return description as a Sting
   */
  public String getDescription() {
    return null;
  }

  public void onButtonClicked() {
  }
}

//  Config view geöffnet dann merge oder rebase bei Dann rebase ohne weiterer Eingabe

//  Zwei Branches setzen darauf dann execute...