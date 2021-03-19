/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
        if (!settingsLoaded || !Settings.getInstance().activeRepositoryIsValid()) {

            persistency.disableUntilSave();
            // Initialize Objects with sane values
            Settings settings = Settings.getInstance();
            Data data = Data.getInstance();
            while (!settings.activeRepositoryIsValid()) {
                c.openDialog(new FirstUseDialogView());
                if (!settings.activeRepositoryIsValid()) {
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
