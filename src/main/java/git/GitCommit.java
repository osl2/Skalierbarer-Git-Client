package git;

import java.util.Date;

public class GitCommit {
  private GitAuthor author;
  private String message;
  private GitCommit[] parents;
  private String hash;
  private boolean isSigned;
  private Date date;

  /* Is only instantiated inside the git Package */
  GitCommit(GitAuthor author, String message, String hash, boolean isSigned) {
    this.author = author;
    this.message = message;
    this.hash = hash;
    this.isSigned = isSigned;
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


  public boolean revert() {
    return false;
  }

  /**
   * Generates the difference between this commit and the one passed
   *
   * @param other the other commit
   * @return String representation of the diff
   */
  public String getDiff(GitCommit other) {
    return null;
  }

  /**
   * Generates the difference to the working directory
   *
   * @return String representation to the working directory
   */
  public String getDiff() {
    return null;
  }

}
