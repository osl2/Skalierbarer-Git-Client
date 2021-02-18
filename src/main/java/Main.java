import controller.GUIController;
import dialogviews.FirstUseDialogView;
import settings.Data;
import settings.Persistency;
import settings.Settings;

import java.io.IOException;
import java.net.URISyntaxException;


public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        Persistency persistency = new Persistency();
        boolean settingsLoaded = persistency.load();
        GUIController c = GUIController.getInstance();
        if (!settingsLoaded) {
            // Initialize Objects with sane values
            Settings settings = Settings.getInstance();
            Data data = Data.getInstance();
            c.openDialog(new FirstUseDialogView());
            settings.setLevel(data.getLevels().getFirst());

            persistency.save();
        }

        c.initializeMainWindow();
        c.restoreDefaultView();
        c.openMainWindow();
        // Load Data -> Persistency
        // Todo: Load Plugins -> ???
        // Apply Plugin changes
        // Open GUI
    }

    public static void restart(String commandLine) {
    }
}
