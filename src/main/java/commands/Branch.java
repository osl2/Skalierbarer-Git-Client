package commands;

import controller.GUIController;
import dialogviews.BranchDialogView;
import git.GitBranch;
import git.GitCommit;
import git.GitFacade;
import git.exception.GitException;

import java.io.IOException;

public class Branch implements ICommand, ICommandGUI {
    private GitCommit commitPointOfBranching;
    private String branchName;


    /**
     * Method to create the new branch.
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        GitFacade jgit = new GitFacade();
        boolean suc = false;

        try {
            suc = jgit.branchOperation(commitPointOfBranching, branchName);
        } catch (GitException e) {
            GUIController.getInstance().errorHandler(e);
        }

        return suc;
    }



    /**
     * Creates with the input the command of the commandline.
     *
     * @return Returns command for Commandline
     */
    public String getCommandLine() {
        return "git branch " + branchName + " " + commitPointOfBranching.getHashAbbrev();
    }

    /**
     * Method to get the name of the Branch command.
     *
     * @return Returns the name of the command
     */
    public String getName() {
        return "Branch";
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
        GUIController.getInstance().openDialog(new BranchDialogView());
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