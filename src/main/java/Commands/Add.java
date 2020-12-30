package Commands;


public class Add implements ICommand, ICommandGUI {

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
     * @return Returns a short description of the command, which can be displayed to the user if necessary
     */
    public String getCommandDescription(){
        return "Fügt Änderungen zur Staging-Area hinzu";
    }

    /**
     * Method to get the Commandline input that would be necessarry to execute the command
     * @return Returns a String representation of the corresponding git command to display on the command line
     */
    public String getCommandLine(String userInput) {
        return null;
    }

    /**
     * Method to get the name of the command, that could be displaied in the GUI
     *
     * @return The name of the command
     */
    public String getName() {
        return null;
    }

    /**
     * Method to get a description of the Command to describe for the user, what the command does
     *
     * @return description as a Sting
     */
    public String getDescription() {
        return null;
    }

}
