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
package settings;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Handles Data-Storage to disk.
 */
public class Persistency extends DataObserver {
    private static final String FILENAME_DATA = "data.json";
    private static final String FILENAME_SETTINGS = "settings.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final File configDir;
    private boolean enabled = true;

    /**
     * Constructor
     *
     * @throws URISyntaxException when the File's path can not be converted to a valid URI
     */
    public Persistency() throws URISyntaxException {
        configDir = new File(
                new File(Persistency.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                        .getParentFile(), // Obtain the folder containing the current JAR
                "config"
        );

        if (!configDir.exists() && !configDir.mkdir()) {
            Logger.getGlobal().warning("Could not create config directory");
        }

        // Pretty-Print
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Fail if config has unknown values
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Disables automatic storage of changed parameters, until save is called manually.
     * Calling {@link #load()} will also re-enable saving.
     */
    public void disableUntilSave() {
        enabled = false;
    }

    /**
     * Saves the current state to Datastore.
     *
     * @param   directory the directory to save the config to.
     * @return  true      if the config was saved successfully
     */
    public boolean save(File directory) {
        enabled = true;
        if (!directory.isDirectory()) {
            Logger.getGlobal().warning(directory.getAbsolutePath() + " is not a directory!");
            return false;
        }
        try {
            mapper.writeValue(new File(directory, FILENAME_DATA), Data.getInstance());
            mapper.writeValue(new File(directory, FILENAME_SETTINGS), Settings.getInstance());
        } catch (IOException e) {
            Logger l = Logger.getGlobal();
            l.warning(e.getMessage());
            return false;
        }
        Settings.getInstance().settingsSavedListener();
        return true;
    }

    /**
     * {@link #load(File)}
     *
     * @return true if the config was saved successfully
     */
    public boolean save() {
        return this.save(configDir);
    }

    /**
     * Load state from Datastore.
     *
     * @param directory the config directory to load files from
     * @return true if the config was loaded successfully
     */
    public boolean load(File directory) {
        enabled = true;
        if (!directory.isDirectory()) {
            Logger.getGlobal().warning(directory.getAbsolutePath() + " is not a directory!");
            return false;
        }
        try {
            mapper.readValue(new File(directory, FILENAME_DATA), Data.class)
                    .addDataChangedListener(this);
            mapper.readValue(new File(directory, FILENAME_SETTINGS), Settings.class)
                    .addDataChangedListener(this);
        } catch (IOException e) {
            Logger l = Logger.getLogger("GitClient");
            l.warning(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * {@link #load(File)}
     *
     * @return true if the config was loaded successfully
     */
    public boolean load() {
        return this.load(configDir);
    }

    @Override
    protected void dataChangedListener(DataObservable observable) {
        if (!enabled) return;
        // We actually don't care who was changed, we just save the current state. Inefficient but easy to implement.
        this.save();
    }
}
