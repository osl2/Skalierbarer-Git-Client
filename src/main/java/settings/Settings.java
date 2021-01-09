package settings;

import Git.GitAuthor;
import Levels.Level;

public class Settings {

    private static Settings INSTANCE = null;
    private Level level;
    private GitAuthor user;
    private boolean useTooltips = true;
    private boolean showTreeView = false;
    private String activeRepositoryPath;

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
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

    public String getActiveRepositoryPath() {
        return activeRepositoryPath;
    }

    public boolean showTreeView() {
        return showTreeView;
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

    public void setShowTreeView(boolean showTreeView) {
        this.showTreeView = showTreeView;
    }

    public void setActiveRepositoryPath(String activeRepositoryPath) {
        this.activeRepositoryPath = activeRepositoryPath;

    }

    public static Settings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Settings();
        }
        return INSTANCE;
    }


}
