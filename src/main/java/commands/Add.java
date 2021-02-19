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

public class Add implements ICommand, ICommandGUI {
  private List<GitFile> files;
  private GitStatus gitStatus;
  private GitData gitData;

  public Add(){
    files = new LinkedList<>();
  }


  /**
   * Performs git add on each GitFile instance separately.
   *
   * @return true, if the command has been executed successfully on every file in the list, false otherwise
   * @throws GitException if command execution in JGit throws an exception
   */
  public boolean execute() {
    List<GitFile> stagedFiles = new LinkedList<>();
    gitData = new GitData();
    gitStatus = gitData.getStatus();

      //get all the staged files
    try {
      stagedFiles = gitStatus.getStagedFiles();
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    } catch (IOException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    }

    //add files that are not in the staging-area yet
    for (GitFile unstagedFile : files){
      /*
      determine whether the file might have been deleted. In this case, call rm() on the GitFile to remove it from the
      list of uncommitted changes. Note: This is a workaround for the fact that manually deleted files cannot be added
      anymore and therefore appear in the status forever
       */
      if (unstagedFile.isDeleted()){
        try {
          unstagedFile.rm();
        } catch (GitException e) {
          e.printStackTrace();
          return false;
        }
      }

      //if file has not been deleted and file is not staged yet, add it to the staging area
      else if (!stagedFiles.contains(unstagedFile)){
        try {
          unstagedFile.add();
        } catch (GitException e) {
          GUIController.getInstance().errorHandler(e);
          return false;
        }
      }
    }
    //remove files that were added to the staging-area earlier but were marked by the user to restage them
    for (GitFile stagedFile : stagedFiles){
      if (!files.contains(stagedFile)){
        try {
          stagedFile.addUndo();
        } catch (GitException e) {
          GUIController.getInstance().errorHandler(e);
          return false;
        }
      }
    }

    return true;
  }




  /**
   * Takes a list of files that should be added to the staging area
   * @param files
   */
  public void addFiles(List<GitFile> files){
    this.files.addAll(files);
  }


  /**
   * Method to get the Commandline input that would be
   *     necessarry to execute the command.
   *
   * @return Returns a String representation of the corresponding
   *     git command to display on the command line
   */
  public String getCommandLine() {
    StringBuffer cl = new StringBuffer("git add ");
    for (GitFile file : files){
      cl.append(file.getPath().getPath() + " ");
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
