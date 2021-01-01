package settings;

import Git.GitAuthor;
import Levels.ILevel;

public class Settings {

    private static Settings INSTANCE = null;
    private ILevel level;
    private GitAuthor user;
    private boolean useTooltips = true;
    private boolean showTreeView = false;

    public ILevel getLevel() {
        return level;
    }

    public void setLevel(ILevel level) {
        this.level = level;
    }

    public GitAuthor getUser() {
        return user;
    }

    public void setUser(GitAuthor user) {
        this.user = user;
    }

    public boolean useTooltips() {
        return useTooltips;
    }

    public void setUseTooltips(boolean useTooltips) {
        this.useTooltips = useTooltips;
    }

    public boolean isShowTreeView() {
        return showTreeView;
    }

    public void setShowTreeView(boolean showTreeView) {
        this.showTreeView = showTreeView;
    }

    /**
     * Private Constructor. This class is instantiated by {@see getInstance()}
     * This class is a SINGLETON
     */
    private Settings() {
        // Load values from file
        // Instantiate correct ILevel
    }

    /**
     * Commits the values of the Settings Object to the settings-file
     */
    public void writeToFile() {

    }

    public static Settings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Settings();
        }
        return INSTANCE;
    }


}
