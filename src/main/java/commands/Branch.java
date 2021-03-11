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
import dialogviews.BranchDialogView;
import git.GitCommit;
import git.GitFacade;
import git.exception.GitException;

/**
 * Represents the git branch operation. With the execute method
 * you can create a new branch in your underlying git repository. In order to
 * execute you have to pass the name of the new Branch and the {@link GitCommit}
 * to start your branch.
 */
public class Branch implements ICommand, ICommandGUI {
    private GitCommit commitPointOfBranching;
    private String branchName;
    private String commandLine;


    /**
     * Method to create the new branch.
     *
     * @return true, if the command has been executed successfully
     */
    @Override
    public boolean execute() {
        GitFacade jgit = new GitFacade();
        boolean suc = false;
        if (commitPointOfBranching == null || branchName == null) {
            GUIController.getInstance().errorHandler("Es muss ein Commit als Startpunkt für den neuen Branch angegeben werden " +
                    "und ein Name für den neuen Branch.");
            return false;
        }

        try {
            suc = jgit.branchOperation(commitPointOfBranching, branchName);
        } catch (GitException e) {
            GUIController.getInstance().errorHandler(e);
        }
        if (suc) {
            commandLine = "git branch " + branchName + " " + commitPointOfBranching.getHashAbbrev();
        }
        return suc;
    }



    /**
     * Creates with the input the command of the commandline.
     *
     * @return Returns command for Commandline
     */
    @Override
    public String getCommandLine() {
        return commandLine;
    }

    /**
     * Method to get the name of the Branch command.
     *
     * @return Returns the name of the command
     */
    @Override
    public String getName() {
        return "Branch";
    }

    /**
     * Method to get a Description of the "Branch" command.
     *
     * @return Returns a Description of what the command is doing
     */
    @Override
    public String getDescription() {
        return "Erstellt einen neuen Änderungszweig an entsprechender Stelle";
    }

    /**
     * OnClick handler for the GUI button representation.
     */
    @Override
    public void onButtonClicked() {
        GUIController.getInstance().openDialog(new BranchDialogView());
    }

    /**
     * Sets the Point of branching to the specified commit.
     *
     * @param commitPointOfBranching Commit at which a new branch is to be created
     */
    public void setBranchPoint(GitCommit commitPointOfBranching) {
        this.commitPointOfBranching = commitPointOfBranching;
    }

    /**
     * Sets the var branchName on the new bramchName.
     *
     * @param branchName New name of the branch
     */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
