package commands;

public class Status implements ICommand {
  /**
   * This method updates the current GitStatus object and calls getters on it
   * TODO: combine getter invocation like template method, implement observer for GitStatus
   *
   * @return
   * @see git.GitStatus
   */
  public boolean execute() {
    return false;
  }

  @Override
  public String getErrorMessage() {
    return null;
  }
}
