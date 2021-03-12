package main;

import controller.GUIController;
import dialogviews.FirstUseDialogView;
import settings.Data;
import settings.Persistency;
import settings.Settings;

import java.net.URISyntaxException;


/**
 * The main entry point of the program. Ties together all subsystems
 */
public class Main {

    /**
     * Initializes the program
     * @param args the commandline args which were passed
     * @throws URISyntaxException if the active repository was an invalid path
     */
    public static void main(String[] args) throws URISyntaxException {
        Persistency persistency = new Persistency();
        boolean settingsLoaded = persistency.load();
        // Don't save during first-use.
        GUIController c = GUIController.getInstance();
        if (!settingsLoaded) {
            persistency.disableUntilSave();
            // Initialize Objects with sane values
            Settings settings = Settings.getInstance();
            Data data = Data.getInstance();
            while (settings.getActiveRepositoryPath() == null) {
                c.openDialog(new FirstUseDialogView());
                if (settings.getActiveRepositoryPath() == null) {
                    c.errorHandler("Die Erstbenutzung muss abgeschlossen werden.");
                }
            }
            settings.setLevel(data.getLevels().get(0));

            // Make sure settings are saved, even on first run.
            settings.addDataChangedListener(persistency);
            data.addDataChangedListener(persistency);
            persistency.save();
        }

        c.initializeMainWindow();
        c.restoreDefaultView();
        c.openMainWindow();
    }
}
