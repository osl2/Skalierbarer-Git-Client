package commands;

import java.io.File;

public class Clone implements ICommand, ICommandGUI {
  private String errorMessage;
  private String commandLine;
  private String commandName;
  private String commandDescription;

  /**
   * Sets a git URL to a remote repository. The input is only valid if
   * the URL is a valid git URL. The definition is found in the official
   * git documentation.
   *
   * @param gitURL is a URL to a remote git repository.
   */
  public void setGitURL(String gitURL) {
  }

  /**
   * Sets the local directory to clone into. Path has to be not null
   * in order to clone successfully.
   * @param path to the local directory.
   */
  public void setDestination(File path){}

  /**
   * Sets whether the clone should be recursive.
   * @param recursive true if the clone is recursive, otherwise false.
   */
  public void cloneRecursive(boolean recursive) {}

  public boolean execute() {
    return false;
  }

  public String getErrorMessage() {
    return null;
  }

  public String getCommandLine() {
    return null;
  }

  public String getName() {
    return null;
  }

  public String getDescription() {
    return null;
  }

  public void onButtonClicked() {

  }
}
