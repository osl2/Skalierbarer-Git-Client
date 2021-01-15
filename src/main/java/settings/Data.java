package settings;


import levels.Level;

import java.util.List;

/**
 * This class holds general purpose data which needs to be persisted
 */
public class Data {

  /**
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
   * @return immutable ordered List of all possible levels in the current configuration
   */
  public List<Level> getLevels() {
    return null;
  }

}
