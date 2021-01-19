package git;

import java.io.File;

public class GitFile {
  private int size;
  private File path;
  /* TODO: SOME WAY TO TRACK CHANGES */

  //for Gitignore.java

  /* Is only instantiated inside the git Package */
  protected GitFile() {
  }

  /**
   * Method to get, if this file is added to the git repository.
   *
   * @return true if file has not been added to the index and file
   *     path matches a pattern in the .gitignore file
   */
  public boolean isIgnoredNotInIndex() {
    return false;
  }

  /**
   * This command returns only files that have been newly
   * created and of whom there is no former version in the index.
   *
   * @return true if file is not being tracked, i.e. git add has never been called on this file
   */
  public boolean isUntracked() {
    return false;
  }

  /**
   * This command returns only files that have been newly created and of whom
   * there is no former version in the index.
   *
   * @return true if file has been newly created and has been added to the staging-area
   */
  public boolean isAdded() {
    return false;
  }

  /**
   * This command returns only files of whom an older version is already in the index.
   *
   * @return true if file is being tracked and there is a modified version in the
   *     working directory which has not been added to the staging-area
   */
  public boolean isModified() {
    return false;
  }

  /**
   * This command returns only files of whom an older version is already in the index.
   *
   * @return true if file has been modified and has been added to the staging-area
   */
  public boolean isChanged() {
    return false;
  }

  /**
   * Adds the file to the staging-area, thereby performing git add
   */
  public void add(){}

  /**
   * Removes file from the staging-area, thereby performing git restore --staged <file>
   */
  public void addUndo(){}
}
