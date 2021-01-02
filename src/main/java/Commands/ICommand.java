package Commands;

/**
 * This interface represents an executable Command.
 * Commands are to be prepared using their custom methods
 * Changes are to be applied to the underlying repository
 * exclusively via the execute() method.
 */
public interface ICommand {

    /**
     * Method to execute the command
     *
     * @return true if the command has been executed successfully \
     * false otherwise
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
