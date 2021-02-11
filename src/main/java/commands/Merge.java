package commands;

import git.GitBranch;
import git.GitChangeConflict;
import git.exception.GitException;
import org.eclipse.jgit.annotations.NonNull;

import java.io.IOException;
import java.util.List;

public class Merge implements ICommand, ICommandGUI {
    private String errorMessage = "";
    private List<GitChangeConflict> conflicts;
    private @NonNull
    GitBranch srcBranch;
    private @NonNull
    GitBranch destBranch;
    private boolean fastForward = true;

    public void setFastForward(boolean fastForward) {
        this.fastForward = fastForward;
    }

    public Merge(@NonNull GitBranch src, @NonNull GitBranch dest) {
        this.srcBranch = src;
        this.destBranch = dest;
    }

    public Merge() {

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
        return null;
    }

    /**
     * This function needs to be called to make sure that all conflicts are resolved before {@link #execute()}
     * can succeed.
     * <p>
     * If necessary a MergeDialogView will be opened to interact with the user.
     */
    public void resolveConflicts() {
        // Open GUI or another way to make sure all conflicts are resolved.
        // Probably a good point for dependency injection in the future.
    }

    /**
     * Method to execute the command.
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() throws GitException, IOException {
        if (this.srcBranch == this.destBranch) {
            // We cannot merge a branch into itself.
            this.errorMessage = "Quell- und Zielzweig können nicht der selbe sein";
            return false;
        }

        this.conflicts = this.srcBranch.merge(this.fastForward);
        if (conflicts.size() > 0) {
            // todo: Call a MergeConflictView here and return its results.
            return false;
        }

        return true;
    }

    public String getErrorMessage() {
        return this.errorMessage;
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
