import controller.GUIController;
import dialogviews.MergeDialogView;
import settings.Settings;

import java.io.File;

public class Main {

  public static void main(String[] args) {
    Settings.getInstance().setActiveRepositoryPath(new File("/home/rad4day/Dokumente/src/fwupd"));
    GUIController c = GUIController.getInstance();
    c.openMainWindow();
    c.openDialog(new MergeDialogView());
    // Load Data -> Persistency
    // Todo: Load Plugins -> ???
    // Apply Plugin changes
    // Open GUI
  }

  public static void restart(String commandLine){}
}
