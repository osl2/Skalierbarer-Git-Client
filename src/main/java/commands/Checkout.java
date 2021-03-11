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
import dialogviews.CheckoutDialogView;
import git.GitBranch;
import git.GitCommit;
import git.GitFacade;
import git.exception.GitException;

/**
 * Represents the git checkout command. In order to execute this command
 * you have to pass the {@link GitBranch} or the {@link GitCommit} you want to checkout.
 */
public class Checkout implements ICommand, ICommandGUI {
    private GitBranch branch;
    private GitCommit commit;

    /**
     * Method to execute the command.
     *
     * @return true, if the command has been executed successfully
     */
    @Override
    public boolean execute() {
        GitFacade facade = new GitFacade();
        if (branch != null) {
            try {
                return facade.checkout(branch);
            } catch (GitException e) {
                GUIController.getInstance().errorHandler(e);
            }
        } else if (commit != null) {
            try {
                return facade.checkout(commit);
            } catch (GitException e) {
                GUIController.getInstance().errorHandler(e);
            }
        }
        GUIController.getInstance().errorHandler("Weder Zweig noch Einbuchung ausgewählt.");
        return false;
    }


    /**
     * Method to get the Commandline input that would be necessary to execute the command.
     *
     * @return Returns a String representation of the corresponding git command to
     * display on the command line
     */
    @Override
    public String getCommandLine() {
        if (branch != null)
            return "git checkout -b " + branch.getName();
        else if (commit != null)
            return "git checkout " + commit.getHashAbbrev();

        return null;
    }

    /**
     * Method to get the name of the command, that could be displayed in the GUI.
     *
     * @return The name of the command
     */
    @Override
    public String getName() {
        return "Checkout";
    }

    /**
     * Method to get a description of the Command to describe for the user, what the command does.
     *
     * @return description as a String
     */
    @Override
    public String getDescription() {
        return "Wechselt auf einen anderen Zweig / eine andere Einbuchung";
    }

    /**
     * OnClick handler for the GUI button representation.
     */
    @Override
    public void onButtonClicked() {
        GUIController.getInstance().openDialog(new CheckoutDialogView());
    }

    /**
     * Defines the target of the executed checkout command.
     * The last setDestination which is called defines the final target.
     *
     * @param commit the commit to check out
     */
    public void setDestination(GitCommit commit) {
        this.branch = null;
        this.commit = commit;
    }

    /**
     * See {@link #setDestination(GitCommit)}
     *
     * @param branch the branch to check out
     */
    public void setDestination(GitBranch branch) {
        this.commit = null;
        this.branch = branch;
    }

}
