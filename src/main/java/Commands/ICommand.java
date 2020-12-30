package Commands;

import Levels.ILevel;

public interface ICommand {

    /**
     * Method to execute the command
     * @return true, if the command has been executed successfully
     */
    boolean execute();


}
