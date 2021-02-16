package commands;

import git.exception.GitException;

/**
 * This interface represents an executable Command.
 * commands are to be prepared using their custom methods
 * Changes are to be applied to the underlying repository
 * exclusively via the execute() method.
 */
public interface ICommand {

  /**
   *Method to execute the command.
   *
   * @return true if the command has been executed successfully \
   *    *     false otherwise
   */
  boolean execute();


}
