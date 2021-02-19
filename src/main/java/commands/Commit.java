package commands;

import controller.GUIController;
import git.GitData;
import git.GitFacade;
import git.GitFile;
import git.GitStatus;
import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import views.AddCommitView;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class Commit implements ICommand, ICommandGUI {
  private String commitMessage;
  private boolean amend;
  private GitFacade gitFacade;
  private GitStatus gitStatus;
  private GitData gitData;

  /**
   * Constructor of the commit command. The amend option must be set explicitly in order to amend the last commit
   */
  public Commit() {
    this.amend = false;
    gitData = new GitData();
    this.gitStatus = gitData.getStatus();
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
   * @throws GitException if the staging-area is empty, if the commit message is missing or if the internal
   * execution of the command in JGit throws an exception
   */
  public boolean execute() {
    gitFacade = new GitFacade();
    List<GitFile> stagedFiles = new LinkedList<>();

    //get the list of staged files from status
    try {
      stagedFiles = gitStatus.getStagedFiles();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } catch (GitException e) {
      e.printStackTrace();
      return false;
    }

    //check if staging-area is empty
    if (stagedFiles.isEmpty()){
      GUIController.getInstance().errorHandler("Staging-Area leer. Leerer Commit nicht erlaubt!");
      return false;
    }

    //prepare the confirmation dialog
    StringBuffer message = new StringBuffer();
    message.append("Bist du sicher, dass die Änderungen an folgenden Dateien eingebucht werden sollen?\n");
    for (GitFile gitFile : stagedFiles){
      message.append(gitFile.getPath().getName() + "\n");
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
    boolean success = false;
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
    return "Commit";
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

  public void onButtonClicked() {
    //do nothing, since there is no commit button
  }
}
