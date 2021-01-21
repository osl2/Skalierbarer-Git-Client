package git;

import java.util.List;

public class GitBranch {
  private GitCommit head;
  private String name;

  /* Is only instantiated inside the git Package */
  GitBranch() {

  }

  /**
   * Merge this branch into b
   *
   * @param b           the branch to be merged into
   * @param fastforward use fast-forward?
   * @return A list of conflicting pieces of code. This list can be empty if the merge is completable
   * without user interaction
   */
  public List<GitChangeConflict> merge(GitCommit b, boolean fastforward) {
    return null;
  }
}
