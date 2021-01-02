package Commands;

import Git.GitBranch;
import Git.GitCommit;

public class Revert implements ICommand, ICommandGUI {
    private GitBranch chosenBranch;
    private GitCommit chosenCommit;

    /**
     * Method to revert back to a chosen commit
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    /**
     * Creates with the input the command of the commandline
     * @param userInput Input off the user
     * @return Returns command for Commandline
     */
    public String getCommandLine(String userInput) {
        return null;
    }
    /**
     *
     * @return Returns the name of the command
     */
    public String getName() {
        return null;
    }
    /**
     *
     * @return Returns a Description of what the command is doing
     */
    public String getDescription() {
        return null;
    }

    @Override
    public void onButtonClicked() {

    }

    /**
     * Sets the variable chosen branch to the new branch
     * @param chosenBranch new value of the variable chosenBranch
     */
    public void setChosenBranch(GitBranch chosenBranch) {
        this.chosenBranch = chosenBranch;
    }

    /**
     * Sets the variable chosenCommit to the new commit
     * @param chosenCommit new value of the variable chosenCommit
     */
    public void setChosenCommit(GitCommit chosenCommit) {
        this.chosenCommit = chosenCommit;
    }

    /**
     *
     * @return Returns the current chosen branch
     */
    public GitBranch getChosenBranch() {
        return chosenBranch;
    }

    /**
     *
     * @return Returns the current chosen commit
     */
    public GitCommit getChosenCommit() {
        return chosenCommit;
    }
}