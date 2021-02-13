package commands;

import git.GitBranch;
import git.GitCommit;

public class Branch implements ICommand, ICommandGUI {
    private GitBranch pointOfBranching;
    private GitCommit commitPointOfBranching;
    private String branchName;


    /**
     * Method to create the new branch.
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
     * Creates with the input the command of the commandline.
     *
     * @return Returns command for Commandline
     */
    public String getCommandLine() {
        return "git branch ";
        // todo: fix
    }

    /**
     * Method to get the name of the Branch command.
     *
     * @return Returns the name of the command
     */
    public String getName() {
        return "branch";
    }

    /**
     * Method to get a Description of the "Branch" command.
     *
     * @return Returns a Description of what the command is doing
     */
    public String getDescription() {
        return "Erstellt einen neuen Änderungszweig an entsprechender Stelle";
    }

    /**
     * OnClick handler for the GUI button representation.
     */
    public void onButtonClicked() {
    }

    /**
     * Method to get the actual commit on witch it is branched.
     *
     * @return Returns the actual Commit on which is branched
     */
    public GitCommit getActualBranch() {
        return commitPointOfBranching;
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