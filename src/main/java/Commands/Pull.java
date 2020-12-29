package Commands;

public class Pull implements ICommand {
    private GitRemote remote;
    private GitBranch remoteBranch;
    /**
     * Performs git pull if allowed, does nothing otherwise
     * @param level The current level
     */
    public void execute(ILevel level){
        if (isAllowed(level)){
            //perform git pull
        }
        else{
            //do nothing
        }
    }

    /**
     * OPTIONAL
     * Needed for command execution, button availability (active/ inactive) etc.
     * @param level The current level
     * @return Returns true, if current level is greater equal than minimum required level (command is allowed), false otherwise
     */
    public boolean isAllowed(ILevel level){
        if (level.getLevelNumber() >= this.getMinimumLevel()){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * OPTIONAL
     * @return The lowest level at which the command can be invoked
     */
    public ILevel getMinimumLevel(){
        //return LevelThree;
    }

    // -------------------------------------------------------------------------------------------------------------
    // The following methods handle the outer representation of the command -
    // perhaps, should be moved to a dedicated class (e.g. AddRepresentation.java)
    // -------------------------------------------------------------------------------------------------------------

    /**
     *
     * @return Returns the short name of the command (e.g. to be displayed on buttons)
     */
    public String getCommandName(){
        return "pull";
    }

    /**
     *
     * @return Returns a String representation of the corresponding git command to display on the command line
     */
    public String getGitCommand(){
        return "git pull";
    }

    /**
     *
     * @return Returns a short description of the command, which can be displayed to the user if necessary
     */
    public String getCommandDescription(){
        return "Lädt Änderungen aus einem Online-Repo und merged sie";
    }

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
}