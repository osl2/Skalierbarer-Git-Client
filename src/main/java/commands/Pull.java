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
import dialogviews.PullConflictDialogView;
import dialogviews.PullDialogView;
import git.GitBranch;
import git.GitData;
import git.GitFacade;
import git.GitRemote;
import git.exception.GitException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the git pull command. In order to execute you have to
 * pass a {@link GitRemote} and a remote {@link GitBranch}.
 */
public class Pull implements ICommand, ICommandGUI {
  private GitRemote remote;
  private GitBranch remoteBranch;
  private String commandLine;


  /**
   * method to set the current remote.
   *
   * @param remote from which files are to be fetched
   */
  public void setRemote(GitRemote remote) {
    this.remote = remote;
  }


  /**
   * Method to set the current remote Branch.
   *
   * @param remoteBranch from which files are to be fetched
   */
  public void setRemoteBranch(GitBranch remoteBranch) {
    this.remoteBranch = remoteBranch;
  }


  @Override
  public String getCommandLine() {
    return commandLine;
  }

  @Override
  public String getName() {
    return "Pull";
  }

  @Override
  public String getDescription() {
    return "Lädt Änderungen aus einem Online-Repo.";
  }


  @Override
  public void onButtonClicked() {
    GitData data = new GitData();
    if (data.getRemotes().isEmpty()) {
      GUIController.getInstance().errorHandler("Es sind keine Remotes bekannt.");
      return;
    }
    GUIController.getInstance().openDialog(new PullDialogView());
  }

  /**
   * Method to execute the command.
   *
   * @return true, if the command has been executed successfully
   */
  @Override
  public boolean execute() {
    if(remote == null || remoteBranch == null) {
      GUIController.getInstance().errorHandler("Es muss ein Remote und ein Branch auf dem Remote übergeben werden.");
      return false;
    }
    GitFacade facade = new GitFacade();
    ArrayList<GitRemote> remoteList = new ArrayList<>();
    remote.addBranch(remoteBranch);
    remoteList.add(remote);
    try {
      facade.fetchRemotes(remoteList);
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    }
    GitData data = new GitData();
    GitBranch dest = null;
    List<GitBranch> allBranches;
    try {
      allBranches = data.getBranches();
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e);
      return false;
    }
    for (GitBranch allBranch : allBranches) {
      // Checks if the fetched branch exists locally.
      if (allBranch.getName().compareTo(remoteBranch.getName()) == 0) {
        dest = allBranch;
      }
    }


    // If fetched branch do not exist locally create new local branch.
    // The new created branch is based on the head commit of the master branch.
    if(dest == null) {
      dest = createLocalBranch(remoteBranch);
    }
    GUIController.getInstance().closeDialogView();
    commandLine = remote.getName() + " " + remoteBranch.getName();
    GUIController.getInstance().openDialog(new PullConflictDialogView(remoteBranch, dest, getCommandLine()));
    return true;
  }

  private GitBranch createLocalBranch(GitBranch master) {
    try {
      GitData data = new GitData();
      GitFacade facade = new GitFacade();
      facade.branchOperation(master.getCommit(), remoteBranch.getName());
      for (GitBranch allBranch : data.getBranches()) {
        if (allBranch.getName().compareTo(remoteBranch.getName()) == 0) {
          return allBranch;
        }
      }
    } catch (GitException e) {
      //TODO: sollte wenn die hier geworfen wird execute nicht false zurückgeben???
      GUIController.getInstance().errorHandler(e);
      return master;
    }
    return master;
  }
}
