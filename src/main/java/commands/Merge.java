package commands;

import git.GitChangeConflict;
import git.GitCommit;

import java.util.List;

public class Merge implements ICommand, ICommandGUI {
    private final GitCommit commitA;
    private final GitCommit commitB;
    private boolean fastForward = true;

    public void setFastForward(boolean fastForward) {
        this.fastForward = fastForward;
    }

    public Merge(GitCommit a, GitCommit b) {
        this.commitA = a;
        this.commitB = b;
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
     * @param userInput The input that the user needs to make additionally to
     *                  the standard output of git commit
     * @return Returns a String representation of the corresponding git command to
     * display on the command line
     */
    public String getCommandLine(String userInput) {
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
