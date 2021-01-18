package git;

public class GitCommit {
  private GitAuthor author;
  private String message;
  private GitCommit[] parents;
  private String hash;
  private boolean isSigned;

  /* See: https://git-scm.com/docs/git-stash#_discussion */
  private boolean isStash;
  private GitTree tree;

  /* Is only instantiated inside the git Package */
  protected GitCommit() {
  }

  public GitAuthor getAuthor() {
    return author;
  }

  public String getMessage() {
    return message;
  }

  public GitCommit[] getParents() {
    return parents;
  }

  public String getHash() {
    return hash;
  }

  public boolean isSigned() {
    return isSigned;
  }

  public boolean isStash() {
    return isStash;
  }

  public GitTree getTree() {
    return tree;
  }

  /* TODO: Diff */
  /* TODO: DiffToWorkDir */
}
