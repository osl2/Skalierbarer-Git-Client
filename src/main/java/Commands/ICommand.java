package Commands;

import Levels.ILevel;

public interface ICommand {

    /**
     * Method to execute the command
     * @param level Competence level that is currently set
     * @return true, if the command has been executed successfully
     */
    boolean execute(ILevel level);

    /**
     * Method to check if a command can be executed in the level
     * @param level Level that needs to be checked
     * @return True if the Command can be executed in that level
     */
    boolean isAllowed(ILevel level);

    /**
     * Method to get the minimum level necessary to execute the command
     * @return Level that is at least necessarry to execute the command
     */
    ILevel getMinimumLevel();

}
