package commands;

import git.GitBranch;
import git.GitChangeConflict;

import java.util.List;

public class Merge implements ICommand, ICommandGUI {
    private GitBranch branchA;
    private GitBranch branchB;
    private boolean fastForward = true;

    public void setFastForward(boolean fastForward) {
        this.fastForward = fastForward;
    }

    public Merge(GitBranch src, GitBranch dest) {
        this.branchA = src;
        this.branchB = dest;
    }

    public Merge() {

    }

    public void setSourceBranch(GitBranch branchA) {
        this.branchA = branchA;
    }

    public void setDestinationBranch(GitBranch branchB) {
        this.branchB = branchB;
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
    public boolean execute() {
        return false;
    }

    public String getErrorMessage() {
        return null;
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
