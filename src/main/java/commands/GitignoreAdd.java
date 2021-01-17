package commands;

import git.GitFile;

/**
 * TODO: Ignorieren intern regeln?.
 */
public class GitignoreAdd implements ICommand {
  private String errorMessage;

  /**
   *
   * @param file The file that should be added to the .gitignore
   */
  public GitignoreAdd(GitFile file){}

  /**
   * This method calls the jgit StatusCommand.setIgnoreSubmodules() method.
   *
   * @return
   */
  public boolean execute() {
    return false;
  }

  public String getErrorMessage() {
    return null;
  }

  /**
   * This method specifies the name or file path of file that should be added to the .gitignore.
   * When execute() is invoked, the specified blob is added to the .gitignore file.
   * Since execute() is invoked for every single
   * blob, this method takes only a single blob instead of a list.
   *
   * @param blob A GitBlob object whose path should be added to the .gitignore
   *             TODO: consider using a list of blobs (concrete files) instead?
   */
  public void setBlob(GitFile blob) {
  }
}
