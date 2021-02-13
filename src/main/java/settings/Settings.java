package settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import git.GitAuthor;
import levels.Level;

import java.io.File;

public class Settings extends DataObservable {

    // Modify settings.PersistencyTest if you add or remove a field!
    private static Settings INSTANCE = null;
    private Level level;
    private GitAuthor user;
    private boolean useTooltips = true;
    private boolean showTreeView = false;
    @JsonIgnore
    private boolean settingsChanged = false;
    private File activeRepositoryPath;

    /**
     * Private Constructor. This class is instantiated by {@see getInstance()}
     * This class is a SINGLETON
     */
    private Settings() {
        // This layout is necessary so that Jackson can create a correctly instantiated class.
        if (INSTANCE == null) {
            INSTANCE = this;
        }
    }

    /**
     * Method to get an instance of the Settings singleton.
     *
     * @return The current Settings object
     */
    public static Settings getInstance() {
        if (INSTANCE == null) {
            new Settings();
        }
        return INSTANCE;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        if (level != this.getLevel()) {
            this.settingsChanged = true;
        }
        this.level = level;
    }

    public GitAuthor getUser() {
        return user;
    }

    public void setUser(GitAuthor user) {
        if (user != this.getUser()) {
            this.settingsChanged = true;
        }
        this.user = user;
    }

    public File getActiveRepositoryPath() {
        return activeRepositoryPath;
    }

    public void setActiveRepositoryPath(File activeRepositoryPath) {
        if (activeRepositoryPath != this.getActiveRepositoryPath()) {
            this.settingsChanged = true;
        }
        this.activeRepositoryPath = activeRepositoryPath;

    }

    public boolean showTreeView() {
        return showTreeView;
    }

    public boolean useTooltips() {
        return useTooltips;
    }

    public void setUseTooltips(boolean useTooltips) {
        if (useTooltips != this.useTooltips) {
            this.settingsChanged = true;
        }
        this.useTooltips = useTooltips;
    }

    public void setShowTreeView(boolean showTreeView) {
        if (showTreeView != this.showTreeView) {
            this.settingsChanged = true;
        }
        this.showTreeView = showTreeView;
    }

    /**
     * Check if the settings have been changed since loading from disk.
     * This is useful to determine if {@link #fireDataChangedEvent()}
     * should be called after making potential changes to the data.
     *
     * @return true if values have been changed and not saved yet
     */
    public boolean settingsChanged() {
        return settingsChanged;
    }

    /**
     * Called when the settings have been saved by {@link Persistency}
     */
    void settingsSavedListener() {
        this.settingsChanged = false;
    }

}
