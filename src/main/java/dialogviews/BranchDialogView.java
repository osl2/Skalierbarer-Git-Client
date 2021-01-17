package dialogviews;

import git.GitBranch;
import git.GitCommit;
import java.util.LinkedList;

public class BranchDialogView implements IDialogView {
  private LinkedList<GitBranch> branches = new LinkedList<GitBranch>();
  private LinkedList<GitCommit> commits = new LinkedList<GitCommit>();
  private String nameOfNew;

  /**
   * Method to get a list of the existing branches.
   *
   * @return Returns the list of the existing branches
   */
  public LinkedList<GitBranch> getBranches() {
    return branches;
  }

  /**
   * Method to set the existing branches.
   *
   * @param branches Input is a new list of branches
   */
  public void setBranches(LinkedList<GitBranch> branches) {
    this.branches = branches;
  }

  /**
   * Method to get a list of the current commits.
   *
   * @return Returns list of current commits
   */
  public LinkedList<GitCommit> getCommits() {
    return commits;
  }

  /**
   * Method to set the list of the commits that are displayed.
   *
   * @param commits Input is new List of commits
   */
  public void setCommits(LinkedList<GitCommit> commits) {
    this.commits = commits;
  }

  /**
   * Method to get the name of the new branch.
   *
   * @return Retruns name of the new branch
   */
  public String getNameOfNew() {
    return nameOfNew;
  }

  /**
   * Method to set the name of a new branch.
   *
   * @param nameOfNew Input is a new name of the next branch (Excetion if "?")
   */
  public void setNameOfNew(String nameOfNew) {
    this.nameOfNew = nameOfNew;
  }

  public void show() {

  }

  public void update() {

  }
}