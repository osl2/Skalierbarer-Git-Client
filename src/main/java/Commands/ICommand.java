package Commands;

import Levels.ILevel;

public interface ICommand {

    /**
     * Method to execute the command
     * @param level Competence level that is currently set
     * @return true, if the command has been executed successfully
     */
    boolean execute();


}
