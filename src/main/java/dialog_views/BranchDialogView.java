package dialog_views;

import git.GitBranch;
import git.GitCommit;

import java.util.LinkedList;

public class BranchDialogView implements IDialogView {
  private LinkedList<GitBranch> branches = new LinkedList<GitBranch>();
  private LinkedList<GitCommit> commits = new LinkedList<GitCommit>();
  private String nameOfNew;

  /**
   * @return Returns the list of the existing branches
   */
  public LinkedList<GitBranch> getBranches() {
    return branches;
  }

  /**
   * @param branches Input is a new list of branches
   */
  public void setBranches(LinkedList<GitBranch> branches) {
    this.branches = branches;
  }

  /**
   * @return Returns list of current commits
   */
  public LinkedList<GitCommit> getCommits() {
    return commits;
  }

  /**
   * @param commits Input is new List of commits
   */
  public void setCommits(LinkedList<GitCommit> commits) {
    this.commits = commits;
  }

  /**
   * @return Retruns name of the new Branch
   */
  public String getNameOfNew() {
    return nameOfNew;
  }

  /**
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