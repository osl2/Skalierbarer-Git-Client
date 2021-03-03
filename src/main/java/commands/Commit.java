package commands;

import controller.GUIController;
import git.GitData;
import git.GitFacade;
import git.GitFile;
import git.GitStatus;
import git.exception.GitException;
import views.AddCommitView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents the git commit command. In order to execute this command
 * at least one file has to be in the staging area and a Commit message has to be passed.
 */
public class Commit implements ICommand, ICommandGUI {
  private String commitMessage;
  private boolean amend;

  /**
   * Constructor of the commit command. The amend option must be set explicitly in order to amend the last commit
   */
  public Commit() {
    this.amend = false;
  }

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
    this.commitMessage = commitMessage;
  }

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   * execution of the command in JGit throws an exception
   */
  public boolean execute() {


    GitFacade gitFacade = new GitFacade();
    GUIController controller = GUIController.getInstance();
    GitData gitData = new GitData();

    //amending is only possible if commit history is not empty
    if (amend && isCommitHistoryEmpty()) {
      controller.errorHandler("Es ist noch kein Commit vorhanden, der rückgängig gemacht werden kann!");
      return false;
    }

    //empty staging area is only allowed for merge and amend
    if (getStagedFiles().isEmpty() && gitData.getMergeCommitMessage() == null && !amend) {
      controller.errorHandler("Staging-Area leer. Leerer Commit nicht erlaubt!");
      return false;
    }


    if (commitMessage == null
            || commitMessage.equals(AddCommitView.DEFAULT_COMMIT_MESSAGE)
            || commitMessage.equals("")){
      controller.errorHandler("Ungültige Commit-Nachricht eingegeben");
      return false;
    }
    boolean success;
    try {
      success = gitFacade.commitOperation(commitMessage, amend);
    } catch (GitException e) {
      controller.errorHandler(e);
      return false;
    }

    return success;
  }

  /**
   * Method to get the Commandline input that would be necessarry to execute the command.
   *
   * @return Returns a String representation of the corresponding git command
   *     to display on the command line
   */
  @Override
  public String getCommandLine() {
    return "git commit " + (amend ? "--amend " : "") + "-m\"" + commitMessage + "\"";
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
    return "Erstellt eine neue Einbuchungen mit den Änderungen aus der " +
            "Staging-Area und der angegebenen Commit-Nachricht";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onButtonClicked() {
    //do nothing, since there is no commit button
  }

  private List<GitFile> getStagedFiles(){
    GitData gitData = new GitData();
    GitStatus gitStatus= gitData.getStatus();
    List<GitFile> stagedFiles;

    //get the list of staged files from status
    try {
      stagedFiles = gitStatus.getStagedFiles();
    } catch (GitException | IOException e) {
      GUIController.getInstance().errorHandler(e);
      stagedFiles = new LinkedList<>();
    }

    return stagedFiles;
  }

  private boolean isCommitHistoryEmpty() {
    GitData gitData = new GitData();
    boolean commitHistoryEmpty = false;
    try {
      commitHistoryEmpty = gitData.getCommits().hasNext();
    } catch (IOException | GitException e) {
      GUIController.getInstance().errorHandler(e);
    }
    return commitHistoryEmpty;
  }
}
