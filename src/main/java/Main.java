import controller.GUIController;
import dialogviews.PullDialogView;
import settings.Settings;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Settings.getInstance().setActiveRepositoryPath(new File("D:\\CloneTestOrdener"));
        GUIController c = GUIController.getInstance();
        c.openMainWindow();
        c.openDialog(new PullDialogView());
        // Load Data -> Persistency
        // Todo: Load Plugins -> ???
        // Apply Plugin changes
        // Open GUI
    }

    public static void restart(String commandLine) {
    }
}
