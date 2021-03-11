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
import dialogviews.MergeDialogView;
import git.GitBranch;
import git.GitData;
import git.GitFileConflict;
import git.exception.GitException;
import org.eclipse.jgit.annotations.NonNull;
import views.AddCommitView;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Merge-Command
 */
public class Merge implements ICommand, ICommandGUI {
    private GitBranch srcBranch;
    private GitBranch destBranch;
    private boolean fastForward = true;

    /**
     * Constructor
     *
     * @param src  branch to merge from
     * @param dest branch to merge into
     */
    public Merge(@NonNull GitBranch src, @NonNull GitBranch dest) {
        this.srcBranch = src;
        this.destBranch = dest;
    }

    /**
     * Constructor without parameters. Needs {@link #setSourceBranch(GitBranch)} and
     * {@link #setDestinationBranch(GitBranch)} before {@link #execute()} may be called.
     */
    public Merge() {

    }

    /**
     * Enable fast forward?
     * <p>
     * Default: true
     *
     * @param fastForward fast-forward enabled or disabled
     */
    public void setFastForward(boolean fastForward) {
        this.fastForward = fastForward;
    }

    /**
     * Set source Branch
     *
     * @param srcBranch branch from which shall be merged
     */
    public void setSourceBranch(@NonNull GitBranch srcBranch) {
        this.srcBranch = srcBranch;
    }

    /**
     * Set destination branch
     *
     * @param destBranch branch to merge into
     */
    public void setDestinationBranch(@NonNull GitBranch destBranch) {
        this.destBranch = destBranch;
    }

    /**
     * Method to execute the command.
     * Different to the definition in {@link ICommand} this function may be called again after resolving conflicts
     * iff the last execution ended in conflicts. To continue the "failed" merge.
     *
     * @return true, if the command has been executed successfully
     */
    @Override
    public boolean execute() {
        GitData gitData = new GitData();
        if (this.srcBranch == null || this.destBranch == null) {
            Logger.getGlobal().warning("Quell- oder Zielzweig wurde nicht gesetzt");
            return false;
        }
        if (this.srcBranch == this.destBranch) {
            // We cannot merge a branch into itself.
            GUIController.getInstance().errorHandler("Quell- und Zielzweig können nicht der selbe sein");
            return false;
        }

        try {
            if (!this.destBranch.equals(gitData.getSelectedBranch())) {
                GUIController.getInstance().errorHandler("Zur Zeit kann nur in den aktuell ausgewählten Zweig gemerged werden.");
                return false;
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Der aktuelle Branch konnte nicht abgefragt werden: {}", e.getMessage());
            return false;
        }

        List<GitFileConflict> conflicts;
        try {
            conflicts = this.srcBranch.merge(this.fastForward);
        } catch (GitException e) {
            GUIController.getInstance().errorHandler(e);
            return false;
        }

        if (conflicts.isEmpty()) return true;

        for (GitFileConflict fileConflict : conflicts) {
            // Resolve Conflicts via Gui
            GUIController.getInstance().openDialog(new MergeConflictDialogView(fileConflict,
                    this.destBranch.getName(), this.srcBranch.getName())
            );
        }

        for (GitFileConflict fileConflict : conflicts) {
            try {
                if (!fileConflict.apply()) return false;
            } catch (IOException | GitException e) {
                GUIController.getInstance().errorHandler(e);
                return false;
            }
        }

        // Everything has been resolved. Create Merge-Commit
        String message = new GitData().getStoredCommitMessage();
        if (message == null) {
            message = "";
        }
        GUIController.getInstance().openView(new AddCommitView(message));


        return true;
    }

    @Override
    public String getCommandLine() {
        if (srcBranch == null) return null;
        return "git merge " + srcBranch.getName();
    }

    @Override
    public String getName() {
        return "Merge";
    }


    @Override
    public String getDescription() {
        return "Verschmilzt zwei Zweige";
    }

    @Override
    public void onButtonClicked() {
        GUIController.getInstance().openDialog(new MergeDialogView());
    }
}
