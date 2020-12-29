package Commands;

import Levels.ILevel;

public class Branch implements ICommand {
    private GitBranch actualBranch;
    private boolean isButton = true;

    /**
     * Performs git branch if allowed, does nothing otherwise
     * @param level The current level
     */

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

    public boolean execute(ILevel level) {
        if (isAllowed(level)){
            //perform git branch
        }
        else{
            //do nothing
        }
    }

    /**
     * OPTIONAL
     * @return The lowest level at which the command can be invoked
     */
    public ILevel getMinimumLevel(){
        //return LevelTwo;
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
        return "branch";
    }

    /**
     *
     * @return Returns a String representation of the corresponding git command to display on the command line
     */
    public String getGitCommand(){
        return "git branch";
    }

    /**
     *
     * @return Returns a short description of the command, which can be displayed to the user if necessary
     */
    public String getCommandDescription(){
        return "Erstellt aktuellen branch mit entsprechenden Namen";
    }

    /**
     *
     * @return Returns true if the command is represanted as Button
     */
    public boolean isButton() {
        return isButton;
    }

    /**
     *
     * @return Returns the actual Branch or commit
     */
    public GitBranch getActualBranch() {
        return actualBranch;
    }

    /**
     *
     * @param actualBranch New Branch the Head points to
     */
    public void setActualBranch(GitBranch actualBranch) {
        this.actualBranch = actualBranch;
    }
}