package commands;

import controller.GUIController;
import dialogviews.MergeConflictDialogView;
import dialogviews.MergeDialogView;
import git.GitBranch;
import git.GitChangeConflict;
import git.GitData;
import git.GitFile;
import git.exception.GitException;
import org.eclipse.jgit.annotations.NonNull;
import views.AddCommitView;

import java.util.List;
import java.util.Map;
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
    public boolean execute() {
        if (this.srcBranch == null || this.destBranch == null) {
            Logger.getGlobal().warning("Source or Destination of Merge was NULL");
            return false;
        }
        if (this.srcBranch == this.destBranch) {
            // We cannot merge a branch into itself.
            GUIController.getInstance().errorHandler("Quell- und Zielzweig können nicht der selbe sein");
            return false;
        }

        Map<GitFile, List<GitChangeConflict>> conflicts;
        try {
            conflicts = this.srcBranch.merge(this.fastForward);
            if (conflicts.size() > 0) {
                for (Map.Entry<GitFile, List<GitChangeConflict>> e : conflicts.entrySet()) {
                    GUIController.getInstance().openDialog(new MergeConflictDialogView(e.getKey(), conflicts,
                            this.destBranch.getName(), this.srcBranch.getName()));
                }

                // Everything has been resolved. Create Merge-Commit
                String message = new GitData().getMergeCommitMessage();
                if (message == null) {
                    message = "";
                }
                GUIController.getInstance().openView(new AddCommitView(message));

            }
        } catch (GitException e) {
            GUIController.getInstance().errorHandler(e);
        }

        return true;
    }


    /**
     * Method to get the Commandline input that would be necessary to execute the command.
     *
     * @return Returns a String representation of the corresponding git command to
     * display on the command line
     */
    public String getCommandLine() {
        return "git merge " + srcBranch.getName();
    }

    /**
     * Method to get the name of the command, that could be displayed in the GUI.
     *
     * @return The name of the command
     */
    @Override
    public String getName() {
        return "Merge";
    }

    /**
     * Method to get a description of the Command to describe for the user,
     * what the command does.
     *
     * @return description as a Sting
     */
    @Override
    public String getDescription() {
        return "Verschmilzt zwei Zweige";
    }

    @Override
    public void onButtonClicked() {
        GUIController.getInstance().openDialog(new MergeDialogView());
    }
}
