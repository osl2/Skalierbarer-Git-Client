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

  /* TODO: Diff */
  /* TODO: DiffToWorkDir */
}
