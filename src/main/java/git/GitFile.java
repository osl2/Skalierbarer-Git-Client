/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
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

  /*
   * Method to get the relative path to the repository.
   * <p><b>WARNING</b>: All File-Separator Characters are replaced by "/"
   * so this function is OS agnostic in its return value.</p>
   *
   */
  protected String getRelativePath() {
    Path filePath = GitData.getRepository().getWorkTree().toPath().relativize(path.toPath());
    String toReturn = filePath.toString();
    if (!File.separator.equals("/")) {
      toReturn = toReturn.replace(File.separator, "/");
    }
    return toReturn;
  }

  /**
   * Returns the relative path to the repository
   * Replaces the internal relative path that uses "/" as separator with the OS-dependent relative path.
   * This method is neccessary to display the correct paths in the command line
   *
   * @return The OS-dependent relative path
   */
  public String getSystemDependentRelativePath() {
    String relativePath = getRelativePath();
    //transform the internal relative path back to the system-dependent path
    if (File.separator.compareTo("/") != 0) {
      relativePath = relativePath.replace("/", File.separator);
    }
    return relativePath;
  }

  /**
   * Method to check if a file is staged.
   *
   * @return True if the file is in the staging-area, false otherwise
   * @throws GitException If status could not be obtained from JGit
   */
  public boolean isStaged() throws GitException {
    Status status = getStatus();
    if (status != null) {
      return status.getAdded().contains(this.getRelativePath())
              || status.getChanged().contains(this.getRelativePath())
              || status.getRemoved().contains(this.getRelativePath());
    } else {
      return false;
    }
  }

  /**
   * Determines if the file has been deleted
   *
   * @return True if the file does not exist in the workspace anymore
   * @throws GitException If status could not be obtained from JGit
   */
  public boolean isDeleted() throws GitException {
    Status status = getStatus();
    if (status != null) {
      return status.getMissing().contains(this.getRelativePath())
              || status.getRemoved().contains(this.getRelativePath());
    } else {
      return false;
    }
  }


  /**
   * Adds the file to the staging-area, thereby performing git add.
   *
   * @return True           if the file was added to the staging area successfully
   * @throws GitException   if the file was not found in git
   */
  public boolean add() throws GitException {
    Git git = GitData.getJGit();
    try {
      String relativePath = this.getRelativePath();
      git.add().addFilepattern(relativePath).call();
      Status status = git.status().call();
      //If file was added, it should now be added. If file was deleted, it should now be removed
      assert status.getAdded().contains(relativePath)
              || status.getRemoved().contains(relativePath)
              || status.getChanged().contains(relativePath);
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
   *
   * @throws GitException   if this file was not found in git.
   */
  public void addUndo() throws GitException {
    Git git = GitData.getJGit();
    try {
      git.reset().addPath(this.getRelativePath()).call();
    } catch (GitAPIException e) {
      throw new GitException("File not found in Git");

    }
  }

  private Status getStatus() throws GitException {
    Status status;
    try {
      status = GitData.getJGit().status().call();
    } catch (GitAPIException e) {
      throw new GitException("Status konnte nicht geladen werden!");
    }
    return status;
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
