package git;

import java.util.List;

/**
 * Represents a git-branch in this program.
 */
public class GitBranch {
  private GitCommit head;
  private String name;

  /* Is only instantiated inside the git Package */
  protected GitBranch() {

  }

  /**
   * Method to set the current value of the Branch name.
   *
   * @param name Name of the branch
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Method to set the current value of the Branch name.
   *
   * @return Name of the branch
   */
  public String getName() {
    return name;
  }

  /**
   * Method to set the head of the commit.
   *
   * @param head Commit that shoud be the new head
   */
  public void setHead(GitCommit head) {
    this.head = head;
  }

  /**
   * Method to get the head of the commit.
   *
   * @return Commit, wich is the head of the branch
   */
  public GitCommit getCommit() {
    return head;
  }

  /**
   * Merge this branch into another one.
   *
   * @param b           the branch to be merged into
   * @param fastforward use fast-forward?
   * @return A list of conflicting pieces of code. This list can be empty if the merge
   * is completable without user interaction
   */
  public List<GitChangeConflict> merge(GitCommit b, boolean fastforward) {
    //TODO: Implementieren!!
    return null;
  }

  @Override
  public boolean equals (Object o){
    if (o.getClass() == this.getClass()){
      GitBranch toCheck = (GitBranch) o;
      if (toCheck.getCommit() == this.getCommit() && toCheck.getName() == this.getName()){
        return true;
      }
    }
    return false;
  }
}
