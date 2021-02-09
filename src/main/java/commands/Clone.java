package commands;

import controller.GUIController;
import dialogviews.CloneDialogView;
import git.GitFacade;

import java.io.File;

public class Clone implements ICommand, ICommandGUI {
  private String errorMessage = "";
  private String commandLine = "git$ clone ";
  private String commandName = "Clone";
  private String commandDescription = "Mit diesem Befehl kann ein entferntes git repository geklont werden.";
  private String gitURL;
  private File path;
  private boolean recursive;

  /**
   * Sets a git URL to a remote repository. The input is only valid if
   * the URL is a valid git URL. The definition is found in the official
   * git documentation.
   *
   * @param gitURL is a URL to a remote git repository.
   */
  public void setGitURL(String gitURL) {
    this.gitURL = gitURL;
  }

  /**
   * Sets the local directory to clone into. Path has to be not null
   * in order to clone successfully.
   * @param path to the local directory.
   */
  public void setDestination(File path) {
    this.path = path;
  }

  /**
   * Sets whether the clone should be recursive.
   * @param recursive true if the clone is recursive, otherwise false.
   */
  public void cloneRecursive(boolean recursive) {
    this.recursive= recursive;
  }

  public boolean execute() {
    if(path == null || gitURL == null) {
      errorMessage = "Es muss eine url angegeben und ein lokaler Pfad ausgewählt werden.";
      return false;
    }
    GitFacade facade = new GitFacade();
    boolean success = facade.cloneRepository(gitURL, path);
    if(!success) {
      errorMessage = "Beim klonen ist ein Fehler aufgetreten.";
      return false;
    }
    commandLine = commandLine + gitURL + ", " + path.getAbsolutePath();
    return true;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getCommandLine() {
    return null;
  }

  public String getName() {
    return commandName;
  }

  public String getDescription() {
    return commandDescription;
  }

  public void onButtonClicked() {
    GUIController.getInstance().openDialog(new CloneDialogView());
  }
}
