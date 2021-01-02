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

    /**
     * Private Constructor. This class is instantiated by {@see getInstance()}
     * This class is a SINGLETON
     */
    private Settings() {
    }

    public GitAuthor getUser() {
        return user;
    }

    public Settings setLevel(ILevel level) {
        this.level = level;

        return this;
    }

    public Settings setUser(GitAuthor user) {
        this.user = user;

        return this;
    }

    public boolean useTooltips() {
        return useTooltips;
    }

    public Settings setUseTooltips(boolean useTooltips) {
        this.useTooltips = useTooltips;

        return this;
    }

    public Settings setShowTreeView(boolean showTreeView) {
        this.showTreeView = showTreeView;

        return this;
    }

    public static Settings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Settings();
        }
        return INSTANCE;
    }


}
