package commands;

import controller.GUIController;
import dialogviews.MergeConflictDialogView;
import git.GitBranch;
import git.GitChangeConflict;
import git.GitFile;
import git.exception.GitException;
import org.eclipse.jgit.annotations.NonNull;

import java.util.List;
import java.util.Map;

public class Merge implements ICommand, ICommandGUI {
    @NonNull
    private GitBranch srcBranch;
    @NonNull
    private GitBranch destBranch;
    private boolean fastForward = true;

    public Merge(@NonNull GitBranch src, @NonNull GitBranch dest) {
        this.srcBranch = src;
        this.destBranch = dest;
    }

    public Merge() {

    }

    public void setFastForward(boolean fastForward) {
        this.fastForward = fastForward;
    }

    public void setSourceBranch(@NonNull GitBranch srcBranch) {
        this.srcBranch = srcBranch;
    }

    public void setDestinationBranch(@NonNull GitBranch destBranch) {
        this.destBranch = destBranch;
    }

    /**
     * Method to get an array of the conflicts that happen during the merge.
     *
     * @return List of the conflicts that happen
     */
    public List<GitChangeConflict> getConflicts() {
     // todo kann weg?
        return null;
    }

    /**
     * This function needs to be called to make sure that all conflicts are resolved before {@link #execute()}
     * can succeed.
     * <p>
     * If necessary a MergeDialogView will be opened to interact with the user.
     */
    public void resolveConflicts() {
        // todo: das wurde auch anders gelöst -> kann weg?
        // Open GUI or another way to make sure all conflicts are resolved.
        // Probably a good point for dependency injection in the future.
    }

    /**
     * Method to execute the command.
     * Different to the definition in {@link ICommand} this function may be called again after resolving conflicts
     * iff the last execution ended in conflicts. To continue the "failed" merge.
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        if (this.srcBranch == this.destBranch) {
            // We cannot merge a branch into itself.
            GUIController.getInstance().errorHandler("Quell- und Zielzweig können nicht der selbe sein");
            return false;
        }

        Map<GitFile, List<GitChangeConflict>> conflicts = null;
        try {
            conflicts = this.srcBranch.merge(this.fastForward);
        } catch (GitException e) {
            e.printStackTrace();
        }
        while (conflicts.size() > 0) {
            // todo: Call a MergeConflictView here and return its results.
            // todo: After merge a new Commit is needed. As we don't set the setCommit(true) flag of JGIT-Merge.
            for (Map.Entry<GitFile, List<GitChangeConflict>> e : conflicts.entrySet())
                GUIController.getInstance().openDialog(new MergeConflictDialogView(e.getKey(), conflicts,
                        this.destBranch.getName(), this.srcBranch.getName()));

            // Everything has been resolved. Create Merge-Commit
            // todo : Load AddCommitView with preloaded Message

            // todo : call merge again and reset conflicts. Should succeed and finish the ongoing merge
            return false;
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
        return null;
    }

    /**
     * Method to get the name of the command, that could be displayed in the GUI.
     *
     * @return The name of the command
     */
    public String getName() {
        return null;
    }

    /**
     * Method to get a description of the Command to describe for the user,
     * what the command does.
     *
     * @return description as a Sting
     */
    public String getDescription() {
        return null;
    }

    public void onButtonClicked() {

    }
}
