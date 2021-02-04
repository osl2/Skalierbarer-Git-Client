package commands;

import git.exception.GitException;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import settings.ICommandDeserializer;
import settings.ICommandSerializer;

/**
 * This interface represents an executable Command.
 * commands are to be prepared using their custom methods
 * Changes are to be applied to the underlying repository
 * exclusively via the execute() method.
 */
@JsonDeserialize(using = ICommandDeserializer.class)
@JsonSerialize(using = ICommandSerializer.class)
public interface ICommand {

  /**
   *Method to execute the command.
   *
   * @return true if the command has been executed successfully \
   *    *     false otherwise
   * @throws GitException If the command could not be performed in JGit internally or if execute() identified missing or
   * misconfigured parameters
   */
  boolean execute() throws GitException;

  /**
   * Returns the error message of commands when there execution was not successful.
   *
   * @return error message, if the command execution was not successful
   * @see #execute()
   */
  String getErrorMessage();


}
