package git;

import java.io.File;
import java.net.URL;
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
     * Create a new Stash
     *
     * @return true iff stash was created successfully
     */
    public boolean createStash() {
        return false;
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

    /**
     * Checkout an other branch. It loads the data of that branch and provides the data from JGit.
     *
     * @param branch branch that should be checked out
     * @return true if it is successfully checked out, false if something went wrong
     */
    public boolean checkout(GitBranch branch) {
        throw new AssertionError("not implemented");
    }

    /**
     * Checkout a commit. It loads the data of the commit and provides the data from JGit
     *
     * @param commit commit that should be checked out
     * @return true if it is performed successfully, false if something went wrong
     */
    public boolean checkout(GitCommit commit) {
        throw new AssertionError("not implemented");
    }

    /**
     * Creates a new branch with the specific name at the commit in JGit
     *
     * @param commit commit where the new branch begins
     * @param name   name of the branch
     * @return true if it is performed successfully, false if something went wrong
     */
    public boolean branchOperation(GitCommit commit, String name) {
        throw new AssertionError("not implemented");
    }

    /**
     * Pulls the files and commits from the branch of the remote to the local repo in Jgit
     *
     * @param remote       remote where the commits come from
     * @param remoteBranch chosen branch where the commits originate from
     * @return true if it is performed successfully, false if something went wrong
     */
    public boolean pullOperation(GitRemote remote, GitBranch remoteBranch) {
        throw new AssertionError("not implemented");
    }

    /**
     * Creates a new Remote in JGit
     *
     * @param name name of the new remote
     * @param url  Url of the repository
     * @return true if it is performed successfully, false if something went wrong
     */
    public boolean remoteAddOperation(String name, URL url) {
        throw new AssertionError("not implemented");
    }

    public boolean initializeRepository(File path) {
        throw new AssertionError("not implemented");
    }

    public boolean cloneRepository(String gitURL, String dest) {
        throw new AssertionError("not implemented");
    }

    public boolean fetchRemotes(List<GitRemote> remotes) {
        throw new AssertionError("not implemented");
    }

    public boolean setRepositoryPath(File path) {
        throw new AssertionError("not implemented");
    }

    /**
     * Commits the files in the staging-area to the git repo and adds the given commit message. If amend is set to true,
     * the last commit is simply amended with the currently added files and the new message
     *
     * @param commitMessage The commit message specified by the user
     * @param amend         true if the last commit should be amended, false otherwise
     * @return True if the commit was successful
     */
    public boolean commitOperation(String commitMessage, boolean amend) {
        throw new AssertionError("not implemented");
    }

    /**
     * Pushes the local commit history to the online repo
     *
     * @param remote The name of the online repo (must have been preconfigured before)
     * @param branch The name of the branch whose commits shoul be pushed
     * @return True if the push has been successful, false otherwise, e.g. connection to online repo failed
     */
    public boolean pushOperation(GitRemote remote, GitBranch branch, boolean follow) {
        throw new AssertionError("not implemented");
    }


    public boolean rebase(GitBranch branchB) {
        throw new AssertionError("not implemented");
    }
}

