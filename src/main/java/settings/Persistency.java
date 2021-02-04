package settings;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Persistency {
    private static final File CONFIG_DIR = new File(System.getProperty("user.home"));
    private static final String FILENAME_DATA = "data.json";
    private static final String FILENAME_SETTINGS = "settings.json";
    private final ObjectMapper mapper;

    public Persistency() {
        this.mapper = new ObjectMapper();

        // Pretty-Print
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Fail if config has unknown values
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Saves the current state to Datastore.
     */
    public boolean save(File configDir) {
        try {
            mapper.writeValue(new File(configDir, FILENAME_DATA), Data.getInstance());
            mapper.writeValue(new File(configDir, FILENAME_SETTINGS), Settings.getInstance());
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
    public boolean save() {
        return this.save(CONFIG_DIR);
    }

    /**
     * Load state from Datastore.
     */
    public boolean load(File configDir) {
        try {
            Data data = mapper.readValue(new File(configDir, FILENAME_DATA), Data.class);
            Settings settings = mapper.readValue(new File(configDir, FILENAME_SETTINGS), Settings.class);
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
        return this.load(CONFIG_DIR);
    }

}
