package Commands;

import Git.GitBranch;
import Git.GitRemote;
import Levels.ILevel;


public class Pull implements ICommand,ICommandGUI {
    private GitRemote remote;
    private GitBranch remoteBranch;

    /**
     *
     * @param remote from which files are to be fetched
     */
    public void setRemote(GitRemote remote){
        this.remote = remote;
    }

    /**
     *
     * @return Returns active repo
     */
    public GitRemote getRemote() {
        return remote;
    }

    /**
     *
     * @param remoteBranch from which files are to be fetched
     */
    public void setRemoteBranch(GitBranch remoteBranch) {
        this.remoteBranch = remoteBranch;
    }

    /**
     *
     * @return Returns the active remoteBranch
     */
    public GitBranch getRemoteBranch() {
        return remoteBranch;
    }

    public String getCommandLine(String userInput) {
        return "git pull";
    }

    public String getName() {
        return "pull";
    }

    public String getDescription() {
        return "Lädt Änderungen aus einem Online-Repo und merged sie";
    }

    @Override
    public void onButtonClicked() {

    }

    /**
     * Method to execute the command
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        //not implemented yet
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}