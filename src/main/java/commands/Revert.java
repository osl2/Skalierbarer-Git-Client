package commands;

import controller.GUIController;
import dialogviews.RevertDialogView;
import git.GitCommit;
import git.GitFacade;
import git.exception.GitException;

/**
 * This class represents the git revert command. In order to execute you have to
 * pass a {@link GitCommit} to revert.
 */
public class Revert implements ICommand, ICommandGUI {
  private GitCommit chosenCommit;
  /**
   * Method to revert back to a chosen commit.
   *
   * @return true, if the command has been executed successfully
   */
  @Override
  public boolean execute() {
    boolean suc = false;
    GitFacade gitFac = new GitFacade();
    try {
      suc = gitFac.revert(chosenCommit);
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e);
    }
    return suc;
  }


  /**
   * Creates with the input the command of the commandline.
   *
   * @return Returns command for Commandline
   */
  @Override
  public String getCommandLine() {
    return " git revert " + chosenCommit.getHashAbbrev();
  }

  /**
   * Method to get the name of the revert command.
   *
   * @return Returns the name of the command
   */
  @Override
  public String getName() {
    return "Revert";
  }

  /**
   * Method to get a description of the revert command.
   *
   * @return Returns a Description of what the command is doing
   */
  @Override
  public String getDescription() {
    return "Macht Änderungen eines ausgewählten Commits rückgängig";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onButtonClicked() {
    GUIController.getInstance().openDialog(new RevertDialogView());
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