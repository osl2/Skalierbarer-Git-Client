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
  public GitCommit[] getCommits() {
    /* Welche Klasse diese Funktionalitäten dann Erzeugt ist noch unklar,
        passiert aber im git package. */
    /* TODO: Andere Datenstruktur? Linked-Lists?   */
    return null;
  }

  /**
   * Get all stashes of the current Repository.
   *
   * @return Array of all Commits which are Stashes
   */
  public GitCommit[] getStashes() {
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
    //TODO: andere Datenstruktur?
    return null;
  }

  /**
   * Method to get a list of the branches, witch are available in the current repository.
   *
   * @return A list of branches in the repository
   */
  public List<GitBranch> getBranches() {
    //TODO: andere Datenstruktur?
    return null;
  }

  /**
   * Method to get list of branches, which are available in the specific online repository
   * @param remote Online repository, where the branches come from
   * @return List of branches in the repository
   */
  public List<GitBranch> getBranches(GitRemote remote){
    return null;
  }

  /**
   * Checkout an other branch. It loads the data of that branch and provides the data from JGit.
   * @param branch branch that should be checked out
   * @return true if it is successfully checked out, false if something went wrong
   */
  public boolean checkout(GitBranch branch) {
    throw new AssertionError("not implemented");
  }

  /**
   * Checkout a commit. It loads the data of the commit and provides the data from JGit
   * @param commit commit that should be checked out
   * @return true if it is performed successfully, false if something went wrong
   */
  public boolean checkout(GitCommit commit) {
    throw new AssertionError("not implemented");
  }

  /**
   * Creates a new branch with the specific name at the commit in JGit
   * @param commit commit where the new branch begins
   * @param name name of the branch
   * @return true if it is performed successfully, false if something went wrong
   */
  public boolean branchOperation(GitCommit commit, String name){
    throw new AssertionError("not implemented");
  }

  /**
   * Pulls the files and commits from the brnach of the remote to the lokal repo in Jgit
   * @param remote remote where the commits come from
   * @param remoteBranch bchosen branch where the commits  come from
   * @return true if it is performed successfully, false if something went wrong
   */
  public boolean pullOperation(GitRemote remote, GitBranch remoteBranch){
    throw new AssertionError("not implemented");
  }

  public boolean initializeRepository (String path){
    throw new AssertionError("not implemented");
  }

  public boolean setRepositoryPath (String path) {
    throw new AssertionError("not implemented");
  }

  /**
   * Commits the files in the staging-area to the git repo and adds the given commit message
   * @param commitMessage The commit message specified by the user
   * @return True if the commit was successful
   */
  public boolean commitOperation(String commitMessage){
    throw new AssertionError("not implemented");
  }
}

