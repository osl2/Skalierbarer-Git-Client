package commands;


import controller.GUIController;
import git.GitData;
import git.GitFile;
import git.GitStatus;
import git.exception.GitException;
import views.AddCommitView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents the git add command. In order to execute this command
 * the user has to pass a list of GitFiles.
 */
public class Add implements ICommand, ICommandGUI {
  private List<GitFile> files;

  public Add(){
    files = new LinkedList<>();
  }


  /**
   * Performs git add on each GitFile instance separately.
   *
   * @return true, if the command has been executed successfully on every file in the list, false otherwise
   */
  public boolean execute() {
    List<GitFile> stagedFiles;
    List<GitFile> filesToBeAdded;
    List<GitFile> filesToBeRestored;
    GitData gitData = new GitData();
    GitStatus gitStatus = gitData.getStatus();

    try {
      stagedFiles = gitStatus.getStagedFiles();
      filesToBeAdded = getFilesToBeAdded(stagedFiles);
      filesToBeRestored = getFilesToBeRestored(stagedFiles);

      //perform add for all files that have been selected by the user
      for (GitFile fileToBeAdded : filesToBeAdded){
        //distinction is necessary, because git add does not worked for files that have been deleted
        if (fileToBeAdded.isDeleted()){
          fileToBeAdded.rm();
        }
        else {
          fileToBeAdded.add();
        }
      }

      //perform add undo for all files that have been deselected
      for (GitFile fileToBeRestored : filesToBeRestored){
        fileToBeRestored.addUndo();
      }
    } catch (GitException | IOException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    }
    return true;
  }

  /*
  Get all files that are currently unstaged and have been selected by the user to be added
   */
  private List<GitFile> getFilesToBeAdded(List<GitFile> stagedFiles){
    List<GitFile> filesToBeAdded = new LinkedList<>();
    for (GitFile fileToBeAdded : files){
      if (!stagedFiles.contains(fileToBeAdded)){
        filesToBeAdded.add(fileToBeAdded);
      }
    }
    return filesToBeAdded;
  }

  /*
  Get all files that are currently staged and have been deselected by the user
   */
  private List<GitFile> getFilesToBeRestored(List<GitFile> stagedFiles){
    List<GitFile> filesToBeRestored = new LinkedList<>();
    for (GitFile fileToBeRestored : stagedFiles){
      if (!files.contains(fileToBeRestored)){
        filesToBeRestored.add(fileToBeRestored);
      }
    }
    return filesToBeRestored;
  }

  /**
   * Takes a list of files that should be added to the staging area.
   * @param files a list of GitFiles to add to the staging area.
   */
  public void setFiles(List<GitFile> files){
    this.files = files;
  }


  /**
   * Method to get the Commandline input that would be
   *     necessarry to execute the command.
   *
   * @return Returns a String representation of the corresponding
   *     git command to display on the command line
   */
  public String getCommandLine() {
    StringBuilder cl = new StringBuilder("git add ");
    for (GitFile file : files){
      cl.append(file.getPath());
      cl.append(" ");
    }
    return cl.toString();
  }

  /**
   * Method to get the name of the command, that could be displayed in the GUI.
   *
   * @return The name of the command
   */
  public String getName() {
    return "Add/Commit";
  }

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a String
   */
  public String getDescription() {
    return "Fügt Dateien zur Staging-Area hinzu";
  }

  public void onButtonClicked() {
    GUIController controller = GUIController.getInstance();
    controller.openView(new AddCommitView());
  }


}
