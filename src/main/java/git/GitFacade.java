package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.PushConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import settings.Settings;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * A class to do operations, that change something in the Git repository.
 */
public class GitFacade {

  /**
   * Create a new Stash.
   * TODO: Klassendiagramm - Parameter neu
   *
   * @return true iff stash was created successfully
   */
  public boolean createStash(String msg) {
    throw new AssertionError("not implemented");
  }

  /**
   * Checkout an other branch. It loads the data of that branch and provides the data from JGit.
   *
   * @param branch branch that should be checked out
   * @return true if it is successfully checked out, false if something went wrong
   */
  public boolean checkout(GitBranch branch) throws GitException {
    try {
      GitData.getJGit().checkout().setCreateBranch(false).setName(branch.getFullName()).call();
      return true;
    } catch (GitAPIException e) {
      throw new GitException(e.getMessage());
    }
  }

  /**
   * Checkout a commit. It loads the data of the commit and provides the data from JGit
   *
   * @param commit commit that should be checked out
   * @return true if it is performed successfully, false if something went wrong
   */
  public boolean checkout(GitCommit commit) throws GitException {
    try {
      GitData.getJGit().checkout().setCreateBranch(false).setName(commit.getHash()).call();
      return true;
    } catch (GitAPIException e) {
      throw new GitException(e.getMessage());
    }
  }

  /**
   * Creates a new branch with the specific name at the commit in JGit. The new branch is checked out afterward
   *
   * @param commit commit where the new branch begins
   * @param name   name of the branch
   * @return true if it is performed successfully, false if something went wrong
   */
  public boolean branchOperation(GitCommit commit, String name) throws GitException {
    try {
      Git git = GitData.getJGit();
      git.checkout().setCreateBranch(true).setName(name).setStartPoint(commit.getHash()).call();
      return true;
    } catch (GitAPIException e) {
      throw new GitException("Fehler beim Erstellen des neuen Branches \n"
          + "Fehlermeldung: " + e.getMessage());
    }
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
  public boolean remoteAddOperation(String name, URL url) throws GitException {
    Git git = GitData.getJGit();
    try {
      git.remoteAdd().setName(name).setUri(new URIish(url)).call();
      return true;
    } catch (GitAPIException e) {
      throw new GitException(e.getMessage());
    }
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
      settings.setActiveRepositoryPath(path);
      GitData data = new GitData();
      data.reinitialize();
    } catch (GitAPIException e) {
      return false;
    }
    return true;
  }

  public boolean cloneRepository(String gitUrl, File dest, boolean recursive) throws GitException {
    try {
      Git git = Git.cloneRepository()
              .setURI(gitUrl)
              .setDirectory(dest)
              .setCloneAllBranches(true)
              .setCredentialsProvider(CredentialProviderHolder.getInstance().getProvider())
              .setCloneSubmodules(recursive)
              .call();
    } catch (JGitInternalException e) {
      throw new GitException("Folgender Fehler ist aufgetreten: " + e.getMessage());
    }catch (TransportException e) {
      throw new GitException("");
    } catch (InvalidRemoteException e) {
      throw new GitException("Es wurde keine korrekte git url angegeben: " + e.getMessage());
    } catch (GitAPIException e) {
      throw new GitException("Folgender Fehler ist aufgetreten: " + e.getMessage());
    }
    return true;
  }

  public boolean fetchRemotes(List<GitRemote> remotes) throws GitException {
    try {
      Git jgit = GitData.getJGit();
      for (int i = 0; i < remotes.size(); i++){
        if (remotes.get(i).getFetchBranches().size() == 0){
          jgit.fetch()
              .setRemote(remotes.get(i).getName())
              .setRefSpecs("refs/heads/*:refs/heads/"
                  + remotes.get(i).getName() + "/*")
              .setCredentialsProvider(CredentialProviderHolder.getInstance().getProvider())
              .call();
        }
        else {
          for (int j = 0; j < remotes.get(i).getFetchBranches().size(); j++){
            GitRemote actualRemote = remotes.get(i);
            GitBranch actualBranch = actualRemote.getFetchBranches().get(j);
            jgit.fetch()
                .setRemote(actualRemote.getName())
                .setRefSpecs("refs/heads/" + actualBranch.getName()  + ":refs/heads/" + actualRemote.getName() + "/"
                    + actualBranch.getName())
                .setCredentialsProvider(CredentialProviderHolder.getInstance().getProvider())
                .call();
          }
        }
      }

    } catch (InvalidRemoteException e) {
      throw new GitException(e.getMessage());
    } catch (TransportException e) {
      throw new GitException(e.getMessage());
    } catch (GitAPIException e) {
      throw new GitException(e.getMessage());
    }
    return true;
  }

  public boolean setRepositoryPath(File path) {
    Settings settings = Settings.getInstance();
    GitData data = new GitData();
    if (RepositoryCache.FileKey.isGitRepository(path, FS.DETECTED)){
      settings.setActiveRepositoryPath(path);
    } else {
      initializeRepository(path);
    }
    data.reinitialize();
    return true;
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
      jgit.commit().setMessage(commitMessage).setAuthor(author.getName(), author.getEmail())
          .setAmend(amend).call();
      return true;
    } catch (GitAPIException e) {
      throw new GitException("Mit dem Commit ist etwas schief gelaufen: \n Fehlermeldung: "
          + e.getMessage());
    }
  }

  /**
   * Pushes the local commit history from the selected branch to the selected online repo and creates a new branch
   * with the same name as the local branch. Sets up the local branch to track the remote branch if 'setUpstream' is true
   *
   * @param remote The name of the online repo (must have been preconfigured before)
   * @param localBranch The name of the local branch whose commits should be pushed
   * @param setUpstream Whether the --upstream flag should be set, i.e. whether a direct connection should be created
   *                    between the local tracking branch and the remote upstream branch
   * @return True if the push has been successful, false otherwise, e.g. connection to
   *     online repo failed
   */
  public boolean pushOperation(GitRemote remote, GitBranch localBranch, boolean setUpstream) throws GitException {
    return pushOperation(remote, localBranch, localBranch, setUpstream);
  }

  /**
   * Pushes the local commit history from the selected branch to the selected online repo in the selected branch. Sets up
   * the local branch to track the remote branch if 'setUpstream' is true
   *
   * @param remote       The remote repo the local changes should be pushed to
   * @param localBranch  The local branch whose changes should be pushed
   * @param remoteBranch The remote branch the changes should be pushed to (already existing)
   * @param follow       Whether the --set-upstream flag should be set, i.e. whether a direct connection should be created
   *                     between the local tracking branch and the remote upstream branch
   * @return True if the push has been executed successfully, false otherwise, e.g. connection to the online repo failed
   */
  public boolean pushOperation(GitRemote remote, GitBranch localBranch, GitBranch remoteBranch, boolean follow) throws GitException {
    try {
      remote.getUrl();
      Git git = GitData.getJGit();
      Repository repository = GitData.getRepository();
      CredentialProviderHolder credentialProvider = CredentialProviderHolder.getInstance();
      UsernamePasswordCredentialsProvider provider = credentialProvider.getProvider();
      Set<String> remoteNames = repository.getRemoteNames();
      if (!remoteNames.contains(remote.getName())) {
        git.remoteAdd().setName(remote.getName()).setUri(new URIish(remote.getUrl())).call();
      }
      RefSpec refSpec = new RefSpec();
      refSpec.setSource(localBranch.getFullName());
      refSpec.setDestination(remoteBranch.getFullName());

      PushConfig pushConfig = new PushConfig();

      git.push()
              .setRemote(remote.getUrl().toString())  //In JGIT uri or name of the remote can be set
              .setCredentialsProvider(provider)
              .setRefSpecs(refSpec)
              .call();
      return true;
    } catch (InvalidRemoteException e) {
      throw new GitException("Remote war ungültig \n" +
              "Fehlermeldung: " + e.getMessage());
    } catch (TransportException e) {
      throw new GitException("Mit der Internet-Verbindung ist etwas schief gelaufen \n" +
              "Fehlermeldung: " + e.getMessage());
    } catch (GitAPIException e) {
      throw new GitException("Ein Fehler ist aufgetreten \n" +
              "Fehlermeldung: " + e.getMessage());
    }
  }


  public boolean rebase(GitBranch branchB) {
    throw new AssertionError("not implemented");
  }

  public boolean revert(GitCommit commit) throws GitException {
    boolean suc;

    suc = commit.revert();
    return suc;
  }

  public String getDiff(GitCommit activeCommit) {
    throw new AssertionError("not implemented");
  }
}
