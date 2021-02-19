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
    GUIController c = GUIController.getInstance();
    c.openMainWindow();
    // Load Data -> Persistency
    // Todo: Load Plugins -> ???
    // Apply Plugin changes
    // Open GUI
  }

  public static void restart(String commandLine){}
}
