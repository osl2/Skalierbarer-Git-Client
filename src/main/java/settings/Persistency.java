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
    private static final ObjectMapper mapper = new ObjectMapper();

    public Persistency() {
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
            Data data = mapper.readValue(new File(directory, FILENAME_DATA), Data.class);
            Settings settings = mapper.readValue(new File(directory, FILENAME_SETTINGS), Settings.class);
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
    public boolean load() throws IOException {
        return this.load(CONFIG_DIR);
    }

}
