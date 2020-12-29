package Commands;

import Levels.ILevel;

public class Add implements ICommand {

    /**
     * Performs git add if allowed, does nothing otherwise
     *
     */
    public boolean execute(){
        return false;
    }

    //Wo soll diese Prüfung vorgenommen werden? Vom Level? Befehl? 


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
