package Commands;

/**
 * Represents the Command in a Command in the Main window
 */
public interface ICommandGUI {


    /**
     * Method to get the Commandline input that would be necessarry to execute the command
     * @param userInput The input that the user needs to make additionally to the standard output of git commit
     * @return Returns a String representation of the corresponding git command to display on the command line
     */
    String getCommandLine(String userInput);

    /**
     * Method to get the name of the command, that could be displaied in the GUI
     * @return The name of the command
     */
    String getName();

    /**
     * Method to get a description of the Command to describe for the user, what the command does
     * @return description as a Sting
     */
    String getDescription();

}