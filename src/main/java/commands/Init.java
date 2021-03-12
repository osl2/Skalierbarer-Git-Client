package commands;

import controller.GUIController;
import git.GitFacade;
import settings.Data;
import settings.Settings;

import javax.swing.*;
import java.io.File;

/**
 * Represents the git init command. In order to execute you have
 * pass a {@link File} which represents a path to a local directory.
 */
public class Init implements ICommand, ICommandGUI {
  private String commandLine;
  private File path = null;

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


  @Override
  public boolean execute() {
    if (path == null) {
      GUIController.getInstance().errorHandler("Es wurde kein Pfad zu einem Ordner übergeben.");
      return false;
    }
    GitFacade facade = new GitFacade();
    boolean success = facade.initializeRepository(path);
    if (!success) {
      GUIController.getInstance().errorHandler("Es konnte am übergebenen Pfad kein git Repository initialisiert werden.");
      return false;
    }
    // Set active repository
    Settings.getInstance().setActiveRepositoryPath(path);
    Data.getInstance().storeNewRepositoryPath(path);

    // Create the git commandLine input to execute this command.
    commandLine = "git init";
    return true;
  }


  @Override
  public String getCommandLine() {
    return commandLine;
  }


  @Override
  public String getName() {
    return "Init";
  }


  @Override
  public String getDescription() {
    return "Der git init Befehl wird verwendet um ein neues git Repository anzulegen. " +
            "Dazu muss in der Kommandozeile der Pfad zu dem gewünschten Ordner angegeben sein.";
  }


  @Override
  public void onButtonClicked() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      path = chooser.getSelectedFile();
      if (!execute()) {
        return;
      }
      GUIController.getInstance().setCommandLine(this.getCommandLine());
    }
  }
}
