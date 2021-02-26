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
     * Saves the current state to Datastore.
     */
    public boolean save(File directory) {
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
     */
    public boolean save() {
        return this.save(configDir);
    }

    /**
     * Load state from Datastore.
     */
    public boolean load(File directory) {
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
     */
    public boolean load() {
        return this.load(configDir);
    }

    @Override
    protected void dataChangedListener(DataObservable observable) {
        // We actually don't care who was changed, we just save the current state. Inefficient but easy to implement.
        this.save();
    }
}
