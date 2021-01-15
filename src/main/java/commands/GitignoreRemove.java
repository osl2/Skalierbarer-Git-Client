package commands;

import git.GitFile;

public class GitignoreRemove implements ICommand {
  /**
   * TODO: find JGit internal method that removes files from .gitignore
   *
   * @return
   */
  public boolean execute() {
    return false;
  }

  @Override
  public String getErrorMessage() {
    return null;
  }

  /**
   * This method specifies the blob that should be removed from the .gitignore file. This should only be possible
   * if the given blob has been added to the .gitignore before. Otherwise, execute() will do nothing/ return false?
   *
   * @param blob The GitBlob object that should be removed from .gitignore
   */
  public void setBLob(GitFile blob) {
  }
}
