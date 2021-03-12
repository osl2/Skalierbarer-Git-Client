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
import dialogviews.CloneDialogView;
import git.CredentialProviderHolder;
import git.GitFacade;
import git.exception.GitException;
import settings.Settings;

import java.io.File;

/**
 * Represents the git clone command. In order to execute this command
 * you have to pass an url and a path to a local directory.
 */
public class Clone implements ICommand, ICommandGUI {
  private String commandLine;
  private String gitURL;
  private File path;
  private boolean recursive = false;

  /**
   * Sets a git URL to a remote repository. The input is only valid if
   * the URL is a valid git URL. The definition is found in the official
   * git documentation.
   *
   * @param gitURL is a URL to a remote git repository.
   */
  public void setGitURL(String gitURL) {
    this.gitURL = gitURL;
  }

  /**
   * Sets the local directory to clone into. Path has to be not null
   * in order to clone successfully.
   * @param path to the local directory.
   */
  public void setDestination(File path) {
    this.path = path;
  }

  /**
   * Sets whether the clone should be recursive.
   * @param recursive true if the clone is recursive, otherwise false.
   */
  public void cloneRecursive(boolean recursive) {
    this.recursive= recursive;
  }

  @Override
  public boolean execute() {
    if(path == null || gitURL == null) {
      GUIController.getInstance().errorHandler("Es muss eine url angegeben und ein lokaler Pfad ausgewählt werden.");
      return false;
    }
    GitFacade facade = new GitFacade();
    try {
      facade.cloneRepository(gitURL, path, recursive);
    } catch (GitException e) {
      if(e.getMessage().compareTo("") == 0) {
        GUIController.getInstance().closeDialogView();
        CredentialProviderHolder.getInstance().changeProvider(true,"");
        GUIController.getInstance().openDialog(new CloneDialogView(gitURL, path, recursive));
      } else {
        GUIController.getInstance().errorHandler(e);
      }
      return false;
    }
    Settings.getInstance().setActiveRepositoryPath(path);
    commandLine = "git clone " + path.getAbsolutePath() + " " + commandLine + gitURL;
    if(recursive) {
      commandLine = commandLine + " --recursive";
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCommandLine() {
    return commandLine;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "Clone";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return "Mit diesem Befehl kann ein entferntes git repository geklont werden.";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onButtonClicked() {
    GUIController.getInstance().openDialog(new CloneDialogView());
  }
}
