package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import settings.Settings;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * A class to do operations, that change something in the Git repository.
 */
public class GitFacade {

  private GitData gitData;

  /**
   * Create a new Stash.
   * TODO: Klassendiagramm - Parameter neu
   *
   * @return true iff stash was created successfully
   */
  public boolean createStash(String msg) {
    return false;
  }

  /**
   * Checkout an other branch. It loads the data of that branch and provides the data from JGit.
   *
   * @param branch branch that should be checked out
   * @return true if it is successfully checked out, false if something went wrong
   */
  public boolean checkout(GitBranch branch) {
    throw new AssertionError("not implemented");
  }

  /**
   * Checkout a commit. It loads the data of the commit and provides the data from JGit
   *
   * @param commit commit that should be checked out
   * @return true if it is performed successfully, false if something went wrong
   */
  public boolean checkout(GitCommit commit) {
    throw new AssertionError("not implemented");
  }

  /**
   * Creates a new branch with the specific name at the commit in JGit.
   *
   * @param commit commit where the new branch begins
   * @param name   name of the branch
   * @return true if it is performed successfully, false if something went wrong
   */
  public boolean branchOperation(GitCommit commit, String name) {
    throw new AssertionError("not implemented");
  }

  /**
   * Pulls the files and commits from the branch of the remote to the local repo in Jgit.
   *
   * @param remote       remote where the commits come from
   * @param remoteBranch chosen branch where the commits originate from
   * @return true if it is performed successfully, false if something went wrong
   */
  public boolean pullOperation(GitRemote remote, GitBranch remoteBranch) {
    throw new AssertionError("not implemented");
  }

  /**
   * Creates a new Remote in JGit.
   *
   * @param name name of the new remote
   * @param url  Url of the repository
   * @return true if it is performed successfully, false if something went wrong
   */
  public boolean remoteAddOperation(String name, URL url) {
    throw new AssertionError("not implemented");
  }

  /**
   * Method to initialize a new git Repositorry on a given path.
   *
   * @param path path, that is needed
   * @return true, if it is done successfully false else
   */
  public boolean initializeRepository(File path) {
    Git git;
    try {
      git = Git.init().setDirectory(path).call();
      Settings settings = Settings.getInstance();
      setRepositoryPath(path);
    } catch (GitAPIException e) {
      return false;
    }
    return true;
  }

  public boolean cloneRepository(String gitUrl, String dest) {
    throw new AssertionError("not implemented");
  }

  public boolean fetchRemotes(List<GitRemote> remotes) {
    throw new AssertionError("not implemented");
  }

  public boolean setRepositoryPath(File path) {
    throw new AssertionError("not implemented");
  }

  /**
   * Commits the files in the staging-area to the git repo and adds the given commit message.
   * If amend is set to true,
   * the last commit is simply amended with the currently added files and the new message
   *
   * @param commitMessage The commit message specified by the user
   * @param amend         true if the last commit should be amended, false otherwise
   * @return True if the commit was successful
   */
  public boolean commitOperation(String commitMessage, boolean amend) throws GitException {
    try {
      Settings settings = Settings.getInstance();
      GitAuthor author = settings.getUser();
      Git jgit = GitData.getJGit();
      jgit.commit().setMessage(commitMessage).setAuthor(author.getName(), author.getEmail()).setAmend(amend).call();
      return true;
    } catch (GitAPIException e) {
      throw new GitException("Mit dem Commit ist etwas schief gelaufen: \n Fehlermeldung: "
          + e.getMessage());
    }
  }

  /**
   * Pushes the local commit history to the online repo.
   *
   * @param remote The name of the online repo (must have been preconfigured before)
   * @param branch The name of the branch whose commits shoul be pushed
   * @return True if the push has been successful, false otherwise, e.g. connection to
   *     online repo failed
   */
  public boolean pushOperation(GitRemote remote, GitBranch branch, boolean follow) {
    throw new AssertionError("not implemented");
  }


  public boolean rebase(GitBranch branchB) {
    throw new AssertionError("not implemented");
  }

  public boolean revert(GitCommit commit) {
    throw new AssertionError("not implemented");
  }

  public String getDiff(GitCommit activeCommit) {
    return null;
  }
}
