package Commands;

public class Add implements ICommand {

    /**
     * Performs git add if allowed, does nothing otherwise
     * @param level The current level
     */
    public void execute(ILevel level){
        if (isAllowed(level)){
            //perform git add
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
    //Wo soll diese Prüfung vorgenommen werden? Vom Level? Befehl? 
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
    //nicht erweiterbar: was, wenn zwischendurch noch ein Level eingeschoben werden soll und add zB erst auf Level 2 unterstützt wird?
    //man müsste jeden einzelnen Befehl ändern!
    public ILevel getMinimumLevel(){
        //return LevelOne;
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
        return "add";
    }

    /**
     *
     * @return Returns a String representation of the corresponding git command to display on the command line
     */
    public String getGitCommand(){
        return "git add";
    }

    /**
     *
     * @return Returns a short description of the command, which can be displayed to the user if necessary
     */
    public String getCommandDescription(){
        return "Fügt Änderungen zur Staging-Area hinzu";
    }
}
