package Commands;

import Levels.ILevel;

public interface ICommand {

    /**
     * Method to execute the command
     * @return true, if the command has been executed successfully
     */
    boolean execute();

    /**
     * Method to undo execute() (if possible)
     * @return true, if the command has been undone successfully
     */
    boolean undo();

    /**
     * @return error message, if the command execution was not successful
     * @see #execute() 
     */
    String getErrorMessage();



}
