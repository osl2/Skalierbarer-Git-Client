package settings;


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
     * Saves a new path to a repository.
     * @param path path to a new created git repository;
     */
    public void saveNewRepositoryPath(String path) {}

}
