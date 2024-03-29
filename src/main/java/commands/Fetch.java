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
import dialogviews.FetchDialogView;
import git.*;
import git.exception.GitException;

import java.util.LinkedList;

/**
 * Represents the git fetch command. In order to execute the command
 * you have to pass a list of {@link GitRemote}.
 */
public class Fetch implements ICommand, ICommandGUI {
  private final LinkedList<GitRemote> remotes = new LinkedList<>();

  /**
   * Executes the "git fetch" command. Can only be used after setRemotes was called once.
   *
   * @return True, if it is successfully executed false if not
   */
  @Override
  public boolean execute()  {
    boolean suc;
      suc = tryFetch();
    return suc;
  }


  /**
   * Add a remote to the list of remotes to fetch.
   * All remoted added via this method will be passed to git-fetch
   *
   * @param remote the GitRemote to add. See {@link GitRemote#addBranch(GitBranch)} for more details
   */
  public void addRemote(GitRemote remote) {
    if (!remotes.contains(remote)) {
      remotes.add(remote);
    }
  }

  /**
   * Adds a Branch on Remote to be fetched by the execute method.
   *
   * @param remote to add a Branch to fetch.
   * @param branch on the Remote to fetch.
   */
  public void addBranch(GitRemote remote, GitBranch branch) {
    if (!remotes.contains(remote)) {
      remotes.add(remote);
    }
    remote.addBranch(branch);
  }

  /**
   * Method to get the Commandline input that would be necessarry to execute the command.
   *
   * @return Returns a String representation of the corresponding
   *     git command to display on the command line
   */
  @Override
  public String getCommandLine() {
    StringBuilder out = new StringBuilder();
    for (GitRemote remote : remotes) {
      if (remote.getFetchBranches().isEmpty()) {
        out.append("git fetch ").append(remote.getName());
      } else {
        for (int j = 0; j < remote.getFetchBranches().size(); j++) {
          out.append(System.lineSeparator()).append("git fetch ").append(remote.getName()).append(" ").append(remote.getFetchBranches().get(j).getName());
        }
      }
    }
    if (out.toString().compareTo("") == 0) {
      return null;
    }
    return out.toString();
  }

  /**
   * Method to get the name of the command, that could be displayed in the GUI.
   *
   * @return The name of the command
   */
  @Override
  public String getName() {
    return "Fetch";
  }

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a Sting
   */
  @Override
  public String getDescription() {
    return "Kommando, welches mehrere Zweige aus mehreren Online-Repositories " +
            "holt, und für diese einen neuen Zweig im aktuellen lokalen Repository anlegt.";
  }

  @Override
  public void onButtonClicked() {
    GitData data = new GitData();
    if (data.getRemotes().isEmpty()) {
      GUIController.getInstance().errorHandler("Es sind keine Remote Repositories bekannt.");
      return;
    }
    FetchDialogView dialogView = new FetchDialogView();
    if (dialogView.canBeOpened()) {
      GUIController.getInstance().openDialog(dialogView);
    }

  }
  private boolean tryFetch(){
    return retryFetch();
  }
  private boolean retryFetch() {
    GitFacade gitFacade = new GitFacade();
    try {
      gitFacade.fetchRemotes(remotes);
      return true;
    } catch (GitException e) {
      CredentialProviderHolder.getInstance().changeProvider(true, "");
      if (CredentialProviderHolder.getInstance().isActive()){
        return tryFetch();
      }
      else{
        CredentialProviderHolder.getInstance().setActive(true);
        return false;
      }
    }
  }
}