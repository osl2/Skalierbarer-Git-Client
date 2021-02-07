package settings;

import git.GitAuthor;
import levels.Level;

import java.io.File;

public class Settings {

  private static Settings INSTANCE = null;
  private Level level;
  private GitAuthor user;
  private boolean useTooltips = true;
  private boolean showTreeView = false;
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
    this.level = level;
  }

  public GitAuthor getUser() {
    return user;
  }

  public void setUser(GitAuthor user) {
    this.user = user;
  }

  public File getActiveRepositoryPath() {
    return activeRepositoryPath;
  }

  public void setActiveRepositoryPath(File activeRepositoryPath) {
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

  /**
   * TODO Was macht methode? Wofür ist sie gedacht?
   * @return
   */
  public boolean settingsChanged() {return false;}

}
