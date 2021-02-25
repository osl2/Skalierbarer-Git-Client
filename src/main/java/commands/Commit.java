package commands;

import controller.GUIController;
import git.GitData;
import git.GitFacade;
import git.GitFile;
import git.GitStatus;
import git.exception.GitException;
import views.AddCommitView;

import javax.swing.*;
import java.io.IOException;
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
    GitData gitData = new GitData();
    GitStatus gitStatus= gitData.getStatus();
    GitFacade gitFacade = new GitFacade();
    List<GitFile> stagedFiles;

    //get the list of staged files from status
    try {
      stagedFiles = gitStatus.getStagedFiles();
    } catch (GitException | IOException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    }

    //check if staging-area is empty
    if (stagedFiles.isEmpty() && gitData.getMergeCommitMessage() == null) {
      GUIController.getInstance().errorHandler("Staging-Area leer. Leerer Commit nicht erlaubt!");
      return false;
    }

    //prepare the confirmation dialog
    StringBuilder message = new StringBuilder();
    message.append("Bist du sicher, dass die Änderungen an folgenden Dateien eingebucht werden sollen?\n");
    for (GitFile gitFile : stagedFiles){
      message.append(gitFile.getPath().getName());
      message.append("\n");
    }
    int commit = JOptionPane.showConfirmDialog(null, message.toString());
    if (commit != 0){
      return false;
    }

    if (commitMessage == null
            || commitMessage.equals(AddCommitView.getDEFAULT_COMMIT_MESSAGE())
            || commitMessage.equals("")){
      GUIController.getInstance().errorHandler("Ungültige Commit-Nachricht eingegeben");
      return false;
    }
    boolean success;
    try {
      success = gitFacade.commitOperation(commitMessage, amend);
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e);
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
  public String getCommandLine() {
    return "git commit " + (amend ? "--amend " : "") + "-m\" ";
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
    return "Erstellt eine neue Einbuchungen mit den Änderungen aus der " +
            "Staging-Area und der angegebenen Commit-Nachricht";
  }

  /**
   * {@inheritDoc}
   */
  public void onButtonClicked() {
    //do nothing, since there is no commit button
  }
}
