package commands;

import controller.GUIController;
import git.*;
import git.exception.GitException;
import views.AddCommitView;

import java.io.IOException;
import java.util.ArrayList;
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
  public void setAmend(boolean amend) {
    this.amend = amend;
    if (amend && commitMessage == null) {
      commitMessage = getLastCommitMessage();
    }
  }

  /**
   * Returns the commit message. If not set, returns the commit message of the last commit
   *
   * @return The commit message
   */
  public String getCommitMessage() {
    if (commitMessage == null) {
      return "";
    } else {
      return commitMessage;
    }
  }

  /**
   * This method sets the commit message of the next commit. The commit message is only updated when the passed message
   * is neither empty nor equals the default commit message
   *
   * @param commitMessage the message of the next commit.
   */
  public void setCommitMessage(String commitMessage) {
    if (commitMessage != null
            && commitMessage.compareTo(AddCommitView.DEFAULT_COMMIT_MESSAGE) != 0
            && commitMessage.compareTo("") != 0) {
      this.commitMessage = commitMessage;
    }
  }

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   * execution of the command in JGit throws an exception
   */
  @Override
  public boolean execute() {


    GitFacade gitFacade = new GitFacade();
    GUIController controller = GUIController.getInstance();
    GitData gitData = new GitData();

    //amending is only possible if commit history is not empty
    if (amend && isCommitHistoryEmpty()) {
      controller.errorHandler("Es ist noch kein Commit vorhanden, der rückgängig gemacht werden kann!");
      return false;
    }

    //commit message must not be empty or the default commit message set by ACV
    if (commitMessage == null) {
      controller.errorHandler("Ungültige Commit-Nachricht eingegeben");
      return false;
    }

    //empty staging area is only allowed for merge and amend
    if (getStagedFiles().isEmpty() && gitData.getMergeMessage() == null && !amend) {
      controller.errorHandler("Staging-Area leer. Leerer Commit nicht erlaubt!");
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

  /*
  Returns a list of staged files from the current status
   */
  private List<GitFile> getStagedFiles() {
    GitData gitData = new GitData();
    GitStatus gitStatus = gitData.getStatus();
    List<GitFile> stagedFiles;

    //get the list of staged files from status
    try {
      stagedFiles = gitStatus.getStagedFiles();
    } catch (GitException | IOException e) {
      GUIController.getInstance().errorHandler(e);
      stagedFiles = new ArrayList<>();
    }

    return stagedFiles;
  }

  /*
  Determines whether the commit history is empty
   */
  private boolean isCommitHistoryEmpty() {
    GitData gitData = new GitData();
    boolean commitHistoryEmpty = false;
    try {
      commitHistoryEmpty = !gitData.getCommits().hasNext();
    } catch (IOException | GitException e) {
      GUIController.getInstance().errorHandler(e);
    }
    return commitHistoryEmpty;
  }

  /*
  Returns the commit message from the last commit. This is necessary for --amend.
   */
  private String getLastCommitMessage() {
    assert !isCommitHistoryEmpty();
    GitData data = new GitData();
    String lastCommitMessage = "";
    try {
      GitCommit lastCommit = data.getCommits().next();
      if (lastCommit != null) {
        lastCommitMessage = lastCommit.getMessage();
      }
    } catch (IOException | GitException e) {
      GUIController.getInstance().errorHandler(e);
    }
    return lastCommitMessage;
  }
}
