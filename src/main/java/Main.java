import controller.GUIController;
import dialogviews.PushDialogView;
import settings.Settings;

import java.io.File;
import java.util.Set;
import dialogviews.BranchDialogView;
import dialogviews.UsernamePasswordDialogView;
import settings.Settings;

import java.io.File;


public class Main {

  public static void main(String[] args) {
    Settings settings = Settings.getInstance();
    settings.setActiveRepositoryPath(new File("C:/Users/henri/PSE/KampFinalProject"));
    GUIController c = GUIController.getInstance();
    c.openMainWindow();
    c.openDialog(new PushDialogView());
    // Load Data -> Persistency
    // Todo: Load Plugins -> ???
    // Apply Plugin changes
    // Open GUI
  }

  public static void restart(String commandLine){}
}
