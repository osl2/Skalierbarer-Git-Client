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
import dialogviews.MergeConflictDialogView;
import dialogviews.RevertDialogView;
import git.GitCommit;
import git.GitData;
import git.GitFileConflict;
import git.exception.GitException;
import views.AddCommitView;

import java.io.IOException;
import java.util.List;

/**
 * Represents the git revert command. In order to execute you have to
 * pass a {@link GitCommit} to revert.
 */
public class Revert implements ICommand, ICommandGUI {
    private GitCommit chosenCommit;
    private String commandLine;

    /**
     * Method to revert back to a chosen commit.
     *
     * @return true, if the command has been executed successfully
     */
    @Override
    public boolean execute() {
        if (chosenCommit == null) {
            GUIController.getInstance().errorHandler("Es muss ein Commit übergebn werden");
            return false;
        }
        try {
            List<GitFileConflict> conflictList = chosenCommit.revert();
            if (!conflictList.isEmpty()) {

                // Resolve Conflicts
                for (GitFileConflict fileConflict : conflictList) {
                    GUIController.getInstance().openDialog(
                            new MergeConflictDialogView(fileConflict, "Jetzige Datei", "Datei nach revert"));
                }
                // Write Conflicts
                for (GitFileConflict fileConflict : conflictList) {
                    if (!fileConflict.apply()) return false;
                }

            }
            // Create commit
            String message = new GitData().getStoredCommitMessage();
            if (message == null) message = "";
            GUIController.getInstance().openView(new AddCommitView(message));
            commandLine = " git revert " + chosenCommit.getHashAbbrev();
            return true;

        } catch (GitException | IOException e) {
            GUIController.getInstance().errorHandler(e);
            return false;
        }
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
     * Method to get the name of the revert command.
     *
     * @return Returns the name of the command
     */
    @Override
    public String getName() {
        return "Revert";
    }

    /**
     * Method to get a description of the revert command.
     *
     * @return Returns a Description of what the command is doing
     */
    @Override
    public String getDescription() {
        return "Macht Änderungen eines ausgewählten Commits rückgängig";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onButtonClicked() {
        GitData data = new GitData();
        try {
            if (data.getBranches().isEmpty()) {
                GUIController.getInstance().errorHandler("Es existiert kein Commit");
                return;
            }
        } catch (GitException e) {
            GUIController.getInstance().errorHandler(e);
            return;
        }
        GUIController.getInstance().openDialog(new RevertDialogView());
    }

    /**
     * Method to get the currently chosen commit.
     *
     * @return Returns the current chosen commit
     */
    public GitCommit getChosenCommit() {
        return chosenCommit;
    }

    /**
     * Sets the variable chosenCommit to the new commit.
     *
     * @param chosenCommit new value of the variable chosenCommit
     */
    public void setChosenCommit(GitCommit chosenCommit) {
        this.chosenCommit = chosenCommit;
    }
}
