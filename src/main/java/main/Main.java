package main;

import controller.GUIController;
import dialogviews.FirstUseDialogView;
import settings.Data;
import settings.Persistency;
import settings.Settings;

import java.net.URISyntaxException;


public class Main {

    public static void main(String[] args) throws URISyntaxException {
        Persistency persistency = new Persistency();
        boolean settingsLoaded = persistency.load();
        GUIController c = GUIController.getInstance();
        if (!settingsLoaded) {
            // Initialize Objects with sane values
            Settings settings = Settings.getInstance();
            Data data = Data.getInstance();
            c.openDialog(new FirstUseDialogView());
            settings.setLevel(data.getLevels().get(0));

            persistency.save();
        }

        c.initializeMainWindow();
        c.restoreDefaultView();
        c.openMainWindow();
    }
}
