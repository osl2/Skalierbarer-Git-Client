package commands;

import java.util.List;

public class Init implements ICommand, ICommandGUI {
  private String errorMessage;
  private String commandLine;
  private String commandName;
  private String commandDescription;

  /**
   * Sets the path to a directory. In this directory a new git repository will be created
   * by calling the execute method. Before execute can be called path has to be a valid
   * path to a directory in the local file system. If the directory is already a
   * git repository the path is treated like a not valid path.
   *
   * @param path to a directory contained in the local file system.
   */
  public void setPathToRepository(String path) {
  }

  /**
   * Returns the paths to all local git repositorys.
   *
   * @return a list containing all paths to the local repositorys.
   */
  public List<String> getRepositorys() {
    return null;
  }

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
