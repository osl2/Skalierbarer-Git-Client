/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
 * Represents the git add command. In order to execute this command
 * the user has to pass a list of GitFiles.
 */
public class Add implements ICommand, ICommandGUI {
  private List<GitFile> selectedFiles;
  private String commandLine = "";

  /**
   * Create a new Instance of the Command.
   *
   * The Command needs to be configured before {@link #execute()} may be called.
   */
  public Add(){
    selectedFiles = new ArrayList<>();
  }


  /**
   * Performs git add on each GitFile instance separately. This method compares the selected files to the files already
   * in the staging area. The user might have deselected files that have been added earlier. This method first adds all
   * files that are not yet staged. Afterwards, all files that were already staged but are not contained in selectedFiles
   * are being removed from the staging are.
   *
   * @return true, if the command has been executed successfully on every file in the list, false otherwise
   */
  @Override
  public boolean execute() {
    try {
      //save the command line output for later
      setCommandLine(getFilesToBeAdded());

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
   * Takes a list of files that should be added with the next execute().This works in both directions: files that
   * do not appear in this list but were staged earlier, will be removed from the staging-area. Files in the list that
   * are not yet staged, will be added.
   *
   * @param selectedFiles All files selected by the user to add them to the staging area
   */
  public void setSelectedFiles(List<GitFile> selectedFiles) {
    this.selectedFiles = selectedFiles;
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
    return commandLine;
  }

  /*
  This is called by execute() to save the data necessary for the command line output for later
   */
  private void setCommandLine(List<GitFile> filesToBeAdded) {
    if (filesToBeAdded.isEmpty()) {
      return;
    } else {
      StringBuilder cl = new StringBuilder("git add ");
      for (GitFile file : filesToBeAdded) {
        cl.append(file.getSystemDependentRelativePath());
        cl.append(" ");
      }
      commandLine = cl.toString();
    }

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

  /**
   * {@inheritDoc}
   */
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
    for (GitFile fileToBeAdded : selectedFiles) {
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
      if (!selectedFiles.contains(fileToBeRestored)) {
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
