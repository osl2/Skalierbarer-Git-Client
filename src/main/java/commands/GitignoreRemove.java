package commands;

import git.GitFile;

public class GitignoreRemove implements ICommand {
  private String errorMessage;
  /**
   * TODO: find JGit internal method that removes files from .gitignore
   *
   * @return True iff Ignoring the file was successful
   */
  public boolean execute() {
    return false;
  }

  /**
   * This method specifies the file that should be removed from the .gitignore file.
   * This should only be possible if the given file has been added to the .gitignore before.
   * Otherwise, execute() will do nothing/ return false?
   *
   * @param file The GitFile object that should be removed from .gitignore
   */
  public void setFile(GitFile file) {
  }
}
