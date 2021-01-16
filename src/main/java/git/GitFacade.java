package git;

import java.util.List;

/**
 * Provides a central point to obtain git objects.
 */
public class GitFacade {

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

  public boolean checkout (GitBranch branch) {
    throw new AssertionError("not implemented");
  }

  public boolean checkout (GitCommit commit){
    throw new AssertionError("not implemented");
  }
}
