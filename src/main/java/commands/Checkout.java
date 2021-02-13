package commands;

import controller.GUIController;
import dialogviews.CheckoutDialogView;
import git.GitBranch;
import git.GitCommit;
import git.GitFacade;
import git.exception.GitException;

public class Checkout implements ICommand, ICommandGUI {
    private String errorMessage;
    private GitBranch branch;
    private GitCommit commit;

    /**
     * Method to execute the command.
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() throws GitException {
        GitFacade facade = new GitFacade();
        if (branch != null) {
            this.errorMessage = "";
            return facade.checkout(branch);
        } else if (commit != null) {
            this.errorMessage = "";
            return facade.checkout(commit);
        }
        //todo: lokalisierung
        this.errorMessage = "Weder Zweig noch Einbuchung ausgewählt.";
        return false;
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
        if (branch != null)
            return "git checkout -b " + branch.getName();
        else if (commit != null)
            return "git checkout " + commit.getHashAbbrev();

        return null;
    }

    /**
     * Method to get the name of the command, that could be displayed in the GUI.
     *
     * @return The name of the command
     */
    public String getName() {
        // TODO: lokalisierung
        return "Checkout";
    }

    /**
     * Method to get a description of the Command to describe for the user, what the command does.
     *
     * @return description as a String
     */
    public String getDescription() {
        // TODO: lokalisierung
        return "Wechselt auf einen anderen Zweig / eine andere Einbuchung";
    }

    public void onButtonClicked() {
        GUIController.getInstance().openDialog(new CheckoutDialogView());
    }

    /**
     * Defines the target of the executed checkout command.
     * The last setDestination which is called defines the final target.
     *
     * @param commit the commit to check out
     */
    public void setDestination(GitCommit commit) {
        this.branch = null;
        this.commit = commit;
    }

    /**
     * See {@link #setDestination(GitCommit)}
     *
     * @param branch the branch to check out
     */
    public void setDestination(GitBranch branch) {
        this.commit = null;
        this.branch = branch;
    }

}