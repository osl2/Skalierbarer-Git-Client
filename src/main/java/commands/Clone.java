package commands;

import controller.GUIController;
import dialogviews.CloneDialogView;
import git.CredentialProviderHolder;
import git.GitFacade;
import git.exception.GitException;
import settings.Settings;

import java.io.File;

public class Clone implements ICommand, ICommandGUI {
  private String commandLine = "git clone ";
  private String commandName = "Clone";
  private String commandDescription = "Mit diesem Befehl kann ein entferntes git repository geklont werden.";
  private String gitURL;
  private File path;
  private boolean recursive = false;

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
      GUIController.getInstance().errorHandler("Es muss eine url angegeben und ein lokaler Pfad ausgewählt werden.");
      return false;
    }
    GitFacade facade = new GitFacade();
    try {
      facade.cloneRepository(gitURL, path, recursive);
    } catch (GitException e) {
      if(e.getMessage().compareTo("") == 0) {
        GUIController.getInstance().closeDialogView();
        CredentialProviderHolder.getInstance().changeProvider(true,"");
        GUIController.getInstance().openDialog(new CloneDialogView(gitURL, path, recursive));
        return false;
      } else {
        GUIController.getInstance().errorHandler(e);
        return false;
      }
    }
    Settings.getInstance().setActiveRepositoryPath(path);
    commandLine = path.getAbsolutePath() + " " + commandLine + gitURL;
    if(recursive) {
      commandLine = commandLine + " --recursive";
    }
    Settings.getInstance().settingsChanged();
    return true;
  }


  public String getCommandLine() {
    return commandLine;
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
