package commands;

public class Clone implements ICommand, ICommandGUI {
  private String errorMessage;
  private String commandLine;
  private String commandName;
  private String commandDescription;

  /**
   * Sets a git URL to a remote repository. The input is only valid if
   * the URL is a valid git URL. The definition is found in the official
   * git documentary.
   *
   * @param gitURL is a URL to a remote git repository.
   */
  public void setGitURL(String gitURL) {
  }

  public void setDestination(String path){}

  public void setPath(String path) {}

  public boolean execute() {
    return false;
  }

  public String getErrorMessage() {
    return null;
  }

  public String getCommandLine(String userInput) {
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
