package commands;


import controller.GUIController;
import git.GitData;
import git.GitFile;
import git.exception.GitException;
import views.AddCommitView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the git add command. In order to execute this command
 * the user has to pass a list of GitFiles.
 */
public class Add implements ICommand, ICommandGUI {
  private List<GitFile> files;

  public Add(){
    files = new ArrayList<>();
  }


  /**
   * Performs git add on each GitFile instance separately.
   *
   * @return true, if the command has been executed successfully on every file in the list, false otherwise
   */
  @Override
  public boolean execute() {
    try {

      //perform add for all files that have been selected by the user
      for (GitFile fileToBeAdded : getFilesToBeAdded()) {
        //distinction is necessary, because git add does not worked for files that have been deleted
        if (fileToBeAdded.isDeleted()) {
          fileToBeAdded.rm();
        } else {
          fileToBeAdded.add();
        }
      }

      //perform add undo for all files that have been deselected
      for (GitFile fileToBeRestored : getFilesToBeRestored()) {
        fileToBeRestored.addUndo();
      }
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    }
    return true;
  }

  /**
   * Takes a list of files that should be added with the next execute()
   *
   * @param files All files selected by the user to add them to the staging area
   */
  public void setFiles(List<GitFile> files) {
    this.files = files;
  }


  /**
   * Method to get the Commandline input that would be
   * necessary to execute the command.
   *
   * @return Returns a String representation of the corresponding
   * git command to display on the command line
   */
  @Override
  public String getCommandLine() {
    StringBuilder cl = new StringBuilder("git add ");
    for (GitFile file : getFilesToBeAdded()) {
      cl.append(file.getSystemDependentRelativePath());
      cl.append(" ");
    }
    return cl.toString();
  }

  /**
   * Method to get the name of the command, that could be displayed in the GUI.
   *
   * @return The name of the command
   */
  @Override
  public String getName() {
    return "Add/Commit";
  }

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a String
   */
  @Override
  public String getDescription() {
    return "Fügt Dateien zur Staging-Area hinzu";
  }

  @Override
  public void onButtonClicked() {
    GUIController controller = GUIController.getInstance();
    controller.openView(new AddCommitView());
  }

  /*
   * This method returns only files that have not yet been staged but were marked by the user to add them to the staging area.
   * The list returned does not contain already staged files.
   * This method is public because it is used by AddCommitView to restore the former state when the user closes the view and
   * does not wish to save his/her changes to the staging-area.
   *
   */
  private List<GitFile> getFilesToBeAdded() {
    GitData data = new GitData();
    List<GitFile> stagedFiles;
    try {
      stagedFiles = data.getStatus().getStagedFiles();
    } catch (IOException | GitException e) {
      GUIController.getInstance().errorHandler(e);
      return new ArrayList<>();
    }

    List<GitFile> filesToBeAdded = new ArrayList<>();
    for (GitFile fileToBeAdded : files) {
      if (!stagedFiles.contains(fileToBeAdded)) {
        filesToBeAdded.add(fileToBeAdded);
      }
    }
    return filesToBeAdded;
  }

  /*
   * This method returns all files that were added to the staging area earlier but have now been de-selected by the user
   * This method is public because it is used by AddCommitView to restore the former state when the user closes the view and
   * does not wish to save his/her changes to the staging-area.
   *
   */
  private List<GitFile> getFilesToBeRestored() {
    GitData data = new GitData();
    List<GitFile> stagedFiles;
    try {
      stagedFiles = data.getStatus().getStagedFiles();
    } catch (IOException | GitException e) {
      GUIController.getInstance().errorHandler(e);
      return new ArrayList<>();
    }


    List<GitFile> filesToBeRestored = new ArrayList<>();
    for (GitFile fileToBeRestored : stagedFiles) {
      if (!files.contains(fileToBeRestored)) {
        filesToBeRestored.add(fileToBeRestored);
      }
    }
    return filesToBeRestored;
  }

  /**
   * This method returns all files whose status has changed. The status of a file can change either because the file
   * was selected by the user and therefore added to the staging-area or because the file was deselected by the user
   * and therefore unstaged
   * @return A list of files whose status will be staged once execute() is called
   */
  public List<GitFile> getFilesStatusChanged(){
    List<GitFile> filesStatusChanged = new ArrayList<>();
    filesStatusChanged.addAll(getFilesToBeAdded());
    filesStatusChanged.addAll(getFilesToBeRestored());
    return filesStatusChanged;
  }


}
