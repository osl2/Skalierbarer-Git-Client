package commands;

import git.GitFile;

public class GitignoreAdd implements ICommand {
  private String errorMessage;
  private GitFile file;

  public GitignoreAdd(GitFile file){
    this.file = file;
  }

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
   * This method specifies the file that should be added to the .gitignore.
   * When execute() is invoked, the specified file is added to the .gitignore file.
   * Since execute() is invoked for every single
   * file, this method takes only a single file instead of a list.
   *
   * @param file A GitFile object whose path should be added to the .gitignore
   */
  public void setFile(GitFile file) {
  }
}
