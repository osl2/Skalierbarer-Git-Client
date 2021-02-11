package settings;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class Persistency {
    private static File CONFIG_DIR;
    private static final String FILENAME_DATA = "data.json";
    private static final String FILENAME_SETTINGS = "settings.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public Persistency() throws URISyntaxException {
        CONFIG_DIR = new File(
                new File(Persistency.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                        .getParentFile(), // Obtain the folder containing the current JAR
                "config"
        );

        if (!CONFIG_DIR.exists()) {
            if (!CONFIG_DIR.mkdir()) {
                Logger.getGlobal().warning("Could not create config directory " + CONFIG_DIR.toString());
            }
        }

        // Pretty-Print
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Fail if config has unknown values
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Saves the current state to Datastore.
     */
    public boolean save(File directory) throws IOException {
        if (!directory.isDirectory())
            throw new IOException(directory.getAbsolutePath() + " is not a directory!");
        try {
            mapper.writeValue(new File(directory, FILENAME_DATA), Data.getInstance());
            mapper.writeValue(new File(directory, FILENAME_SETTINGS), Settings.getInstance());
        } catch (IOException e) {
            Logger l = Logger.getGlobal();
            l.warning(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * {@link #load(File)}
     */
    public boolean save() throws IOException {
        return this.save(CONFIG_DIR);
    }

    /**
     * Load state from Datastore.
     */
    public boolean load(File directory) throws IOException {
        if (!directory.isDirectory())
            throw new IOException(directory.getAbsolutePath() + " is not a directory!");
        try {
            mapper.readValue(new File(directory, FILENAME_DATA), Data.class);
            mapper.readValue(new File(directory, FILENAME_SETTINGS), Settings.class);
        } catch (IOException e) {
            Logger l = Logger.getLogger("GitClient");
            l.warning(e.getMessage());
            return false;
        }
        Settings.getInstance().settingsSavedListener();
        return true;
    }

    /**
     * {@link #load(File)}
     */
    public boolean load() throws IOException {
        return this.load(CONFIG_DIR);
    }

}
