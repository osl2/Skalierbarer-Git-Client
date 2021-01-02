package Commands;


public class Add implements ICommand, ICommandGUI {
        // Todo: Change to custom methods as discussed on 2021-01-02
    /**
     * Performs git add
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        return false;
    }

    /**
     * This method passes a list of String parameters to the command, if required. If no parameters are required,
     * setParameters() does nothing.
     * The git add command needs one or several blobs (changes in files) in order to be executed. When execute() is
     * invoked, the given blobs are added to the staging area.
     * @param blobs A list of String patterns for blobs (changes in files) that should be added to the staging area. Must
     *              contain at least one valid pattern.
     * @see #hasParameters()
     */
    //sollte in IF geschoben werden, aber mit welchem Datentyp? Ideal wäre eigentlich GitBlob
    public void setParameters(List<String> blobs){}

    /**
     * This method specifies whether the git add needs parameters in order to be executed
     * @return true, if the command needs parameters to be executed
     * @see #setParameters(List)
     */
    //Prüfung hier oder erst bei Aufruf von execute() --> private?
    public boolean hasParameters(){
        return false;
    }

    /**
     * This method passes a list of options to the command, if allowed. When the command does not support any options,
     * setOptions() does nothing.
     * The git add command does not support any options.
     * @param options A list of String options that describe the way in which the command should be executed.
     * @see #hasOptions()
     */
    //sollte in IF geschoben werden, welcher Datentyp?
    public void setOptions(List<String> options){}

    /**
     * This method specifies whether the git command supports options.
     * @return true, if the command can be further specified by passing one or more options.
     * @see #setOptions(List)
     */
    //Prüfung hier oder erst bei Aufruf von execute() --> private?
    public boolean hasOptions() {
        return false;
    }

    /**
     * This method returns the error message in case the command has not been executed successfully.
     * @return empty String if the command was executed successfully, the error String otherwise
     */
    //Fehlerbehandlung direkt in execute() --> private machen?
    public String getErrorMessage() {
        return null;
    }

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
