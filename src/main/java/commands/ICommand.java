package commands;

/**
 * This interface represents an executable Command.
 * commands are to be prepared using their custom methods
 * Changes are to be applied to the underlying repository
 * exclusively via the execute() method.
 */
public interface ICommand {

  /**
   * Method to execute the command.
   *
   * @return true if the command has been executed successfully \
   * false otherwise
   */
  boolean execute();

  /**
   * Returns the error message of commands when there execution was not successful.
   *
   * @return error message, if the command execution was not successful
   * @see #execute()
   */
  String getErrorMessage();


}
