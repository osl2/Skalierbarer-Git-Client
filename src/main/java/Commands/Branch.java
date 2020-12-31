package Commands;

import Git.GitBranch;
import Git.GitCommit;

public class Branch implements ICommand, ICommandGUI {
    private GitBranch pointOfBranching;
    private GitCommit commitPointOfBranching;
    private String branchName;
    private boolean isButton = true;



    /**
     * Method to create the new branch
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        return false;
    }

    /**
     * Creates with the input the command of the commandline
     * @param userInput Input off the user
     * @return Returns command for Commandline
     */
    public String getCommandLine(String userInput) {
        return "git branch " + userInput;
    }
    /**
     *
     * @return Returns the name of the command
     */
    public String getName() {
        return "branch";
    }
    /**
     *
     * @return Returns a Description of what the command is doing
     */
    public String getDescription() {
        return "Erstellt einen neuen Änderungszweig an entsprechender Stelle";
    }
    /**
     *
     * @return Returns true if the command is represented as Button
     */
    public boolean isButton() {
        return isButton;
    }

    /**
     *
     * @return Returns the actual Commit on which is branched
     */
    public GitCommit getActualBranch() {
        return commitPointOfBranching;
    }

    /**
     * Sets the Point of branching to the specified commit
     * @param commitPointOfBranching Commit at which a new branch is to be created
     */
    public void setBranchPoint(GitCommit commitPointOfBranching) {
        this.commitPointOfBranching = commitPointOfBranching;
    }

}