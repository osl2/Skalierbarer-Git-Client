package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import settings.Settings;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents a GitFile in the program.
 */
public class GitFile {
  private final long size;
  private final File path;
  private boolean ignored;
  private boolean staged;
  private boolean deleted;

  /**
   * Method to generate a new GitFile, can only be instantiated in the git-package.
   *
   * @param size size of the file
   * @param path path of the file
   */
  GitFile(long size, File path) {
    if (!path.getAbsolutePath().startsWith(Settings.getInstance().getActiveRepositoryPath()
        .getAbsolutePath())) {
      throw new AssertionError("File is not located in the repository directory!");
    }
    this.size = size;
    this.path = path;
    this.staged = false;
  }

  /**
   * Method to get the size of the file.
   *
   * @return Size of the file
   */
  public long getSize() {
    return this.size;
  }

  /**
   * Method to get the path of the GitFile.
   *
   * @return path of the File
   */
  public File getPath() {
    return this.path;
  }

  /**
   * Method to get the relative path to the repository.
   * <p><b>WARNING</b>: All File-Separator Characters are replaced by "/"
   * so this function is OS agnostic in its return value.</p>
   *
   * @return relative path as a String
   */
  public String getRelativePath() {
    Path filePath = GitData.getRepository().getWorkTree().toPath().relativize(path.toPath());
    String toReturn = filePath.toString();
    if (!File.separator.equals("/")) {
      toReturn = toReturn.replace(File.separator, "/");
    }
    return toReturn;
  }

  /**
   * Adds or removes the file from the .gitignore.
   *
   * @param ignored Whether the file should be added to the .gitignore or be removed from it
   */
  @SuppressWarnings("unused")
  public void setIgnored(boolean ignored) {
    this.ignored = ignored;
  }

  /**
   * Method to get, if the file is ignored.
   *
   * @return true if file has not been added to the index and file
   * path matches a pattern in the .gitignore file
   */
  @SuppressWarnings("unused")
  public boolean isIgnoredNotInIndex() {
    return ignored;
  }

  /**
   * Method to check if a file is staged.
   *
   * @return True if the file is in the staging-area, false otherwise
   */
  public boolean isStaged() {
    return staged;
  }

  /**
   * Sets the internal state 'staged' to true if the file is in the staging area (added or changed),
   * false otherwise. Called by GitStatus when GitFile is created. This method is necessary for git
   * diff
   *
   * @param staged Whether the file is in the staging-area
   * @see GitCommit#getDiff(GitFile)
   */
  public void setStaged(boolean staged) {
    this.staged = staged;
  }

  /**
   * Determines if the file is deleted.
   *
   * @return True if the file does not exist in the workspace anymore
   */
  public boolean isDeleted() {
    return deleted;
  }

  /**
   * Sets the internal state of the file to 'deleted' if its nested File object does not exist.
   *
   * @param deleted Whether the file has been deleted
   */
  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  /**
   * Adds the file to the staging-area, thereby performing git add.
   *
   * @return True if the file was added to the staging area successfully
   */
  public boolean add() throws GitException {
    Git git = GitData.getJGit();
    try {
      git.add().addFilepattern(this.getRelativePath()).call();
      assert git.status().call().getAdded().contains(this.getRelativePath());
      assert !git.status().call().getAdded().isEmpty();
      return true;
    } catch (GitAPIException e) {
      throw new GitException("File not found in Git");

    }
  }


  /**
   * Adds a remove operation for that file to the staging area.
   *
   * @throws GitException if a file could not be marked as removed.
   */
  public void rm() throws GitException {
    Git git = GitData.getJGit();
    try {
      git.rm().addFilepattern(this.getRelativePath()).call();
    } catch (GitAPIException e) {
      throw new GitException("File could not be removed in Git");
    }
  }

  /**
   * Removes file from the staging-area, thereby performing git restore --staged file.
   */

  public void addUndo() throws GitException {
    Git git = GitData.getJGit();
    try {
      git.reset().addPath(this.getRelativePath()).call();
    } catch (GitAPIException e) {
      throw new GitException("File not found in Git");

    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GitFile gitFile = (GitFile) o;
    return Objects.equals(path, gitFile.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path);
  }
}
