package commands;

import git.GitFacade;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class Init implements ICommand, ICommandGUI {
  private String errorMessage = "";
  private String commandLine = "";
  private String commandName = "Init";
  private String commandDescription = "Der git init Befehl wird verwendet um ein neues git Repository anzulegen." +
          "Dazu muss in der Kommandozeile der Pfad zu dem gewünschten Ordner angegeben sein.";
  private File path = null;
  private GitFacade facade;
  private JFileChooser chooser;

  /**
   * Sets the path to a directory. In this directory a new git repository will be created
   * by calling the execute method. Before execute can be called path has to be a valid
   * path to a directory in the local file system. If the directory is already a
   * git repository the path is treated like a not valid path.
   *
   * @param path to a directory contained in the local file system.
   */
  public void setPathToRepository(File path) {
    this.path = path;
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
    if(path == null) {
      errorMessage = "Es wurde kein Pfad zu einem Ordner übergeben.";
      return false;
    } // Was machen wir wenn in dem übergebenen Ordner bereits ein repo existiert?
    facade = new GitFacade();
    boolean success = facade.initializeRepository(path);
    if(!success) {
      errorMessage = "Es konnte am übergebenen Pfad kein git Repository initialisiertw werden.";
    }
    // Create the git commandLine input to execute this command.
    commandLine = path.getAbsolutePath() + " git init";
    return success;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getCommandLine(String userInput) {
    return commandLine;
  }

  public String getName() {
    return commandName;
  }

  public String getDescription() {
    return commandDescription;
  }

  public void onButtonClicked() {
    chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = chooser.showOpenDialog(null);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      path = chooser.getSelectedFile();
    }
  }
}
