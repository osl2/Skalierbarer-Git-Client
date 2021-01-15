package settings;

import java.util.List;
import levels.Level;

/**
 * This class holds general purpose data which needs to be persisted.
 */
public class Data {

  /**
   * Method to get A list of all repositories that have been opened before.
   *
   * @return list of paths ordered by time last opened ascending
   */
  public List<String> getRecentlyOpenedRepositories() {
    return null;
  }

  /**
   * Stores a new path to a repository.
   *
   * @param path path to a new created git repository;
   */
  public void storeNewRepositoryPath(String path) {
  }

  /**
   * Method to get a list of all possible levels in the current configuration.
   *
   * @return immutable ordered List of all possible levels in the current configuration
   */
  public List<Level> getLevels() {
    return null;
  }

}
