package git;

import java.util.List;

/**
 * Provides a central point to obtain and create a number of git objects.
 */
public class Git {

    /**
     * Get all commits of the current Repository.
     *
     * @return Array of all commits without stashes
     */
    public List<GitCommit> getCommits() {
        return null;
    }

    /**
     * Get all stashes of the current Repository.
     *
     * @return A list of all Stashes
     */
    public List<GitStash> getStashes() {
        return null;
    }


    /**
     * Get the current status of the working directory.
     * Holds information about all active files and their current stages
     *
     * @return The singleton status object
     */
    public GitStatus getStatus() {
        return null;
    }

    /**
     * Get a list of all configured remotes.
     *
     * @return A list of all remotes
     */
    public List<GitRemote> getRemotes() {
        return null;
    }

    /**
     * Method to get a list of the branches, witch are available in the current repository.
     *
     * @return A list of branches in the repository
     */
    public List<GitBranch> getBranches() {
        return null;
    }

    /**
     * Method to get list of branches, which are available in the specific online repository
     *
     * @param remote Online repository, where the branches come from
     * @return List of branches in the repository
     */
    public List<GitBranch> getBranches(GitRemote remote) {
        return null;
    }
}

