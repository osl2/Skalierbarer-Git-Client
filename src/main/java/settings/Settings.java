package settings;

import git.GitAuthor;
import levels.Level;

public class Settings {

  private static Settings INSTANCE = null;
  private Level level;
  private GitAuthor user;
  private boolean useTooltips = true;
  private boolean showTreeView = false;
  private String activeRepositoryPath;

  /**
   * Private Constructor. This class is instantiated by {@see getInstance()}
   * This class is a SINGLETON
   */
  private Settings() {
  }

  /**
   * Method to get an instance of the Settings singleton.
   *
   * @return The current Settings object
   */
  public static Settings getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new Settings();
    }
    return INSTANCE;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public GitAuthor getUser() {
    return user;
  }

  public void setUser(GitAuthor user) {
    this.user = user;
  }

  public String getActiveRepositoryPath() {
    return activeRepositoryPath;
  }

  public void setActiveRepositoryPath(String activeRepositoryPath) {
    this.activeRepositoryPath = activeRepositoryPath;

  }

  public boolean showTreeView() {
    return showTreeView;
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

  public boolean settingsChanged() {return false;}
}
