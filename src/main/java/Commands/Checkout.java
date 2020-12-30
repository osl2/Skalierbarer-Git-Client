package Commands;

import Levels.ILevel;

public class Checkout implements ICommand, ICommandGUI{

    /**
     * Method to execute the command
     *
     * @param level Competence level that is currently set
     * @return true, if the command has been executed successfully
     */
    public boolean execute(ILevel level) {
        //not implemented yet
        return false;
    }

    /**
     * Method to check if a command can be executed in the level
     *
     * @param level Level that needs to be checked
     * @return True if the Command can be executed in that level
     */
    public boolean isAllowed(ILevel level) {
        //not implemented yet
        return false;
    }

    /**
     * Method to get the minimum level necessary to execute the command
     *
     * @return Level that is at least necessarry to execute the command
     */
    public ILevel getMinimumLevel() {
        //not implemented yet
        return null;
    }

    /**
     * Method to execute the command
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        return false;
    }

    /**
     * Method to get the Commandline input that would be necessarry to execute the command
     *
     * @param userInput Input wich the user has to make individually for executing the command
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