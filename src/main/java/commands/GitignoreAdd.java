package commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import git.GitFile;

public class GitignoreAdd implements ICommand {
  // TODO: Mit GitignoreRemove zusammenfassen?
  private String errorMessage;
  private GitFile file;

  public GitignoreAdd(GitFile file) {
    this.file = file;
  }

  @JsonCreator
  public GitignoreAdd() {
    /* Used by Jackson to create object for Level */
    this.file = null;
  }

  /**
   * This method calls the jgit StatusCommand.setIgnoreSubmodules() method.
   *
   * @return true iff Add was successful
   */
  public boolean execute() {
    return false;
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
