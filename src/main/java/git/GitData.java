package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.StashListCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import settings.Settings;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Provides a central point to obtain and create a number of git objects.
 */
public class GitData {

  private static org.eclipse.jgit.api.Git git;
  private static Repository repository;
  private static final String ERROR_MESSAGE = "Fehlermeldung: ";

  /**
   * Method to initialize GitData, it is accessible in the whole project.
   */
  public GitData() {
    if (git == null || repository == null) {
      initializeRepository();
    }
  }

  /**
   * Method to get the repository, it is only used in the git-package.
   *
   * @return repository.
   */
  static Repository getRepository() {
    return repository;
  }

  /**
   * Method to get the JGit-Instance.
   *
   * @return Instance of Jgit
   */
  static Git getJGit() {
    return git;
  }

  /**
   * Method to get the currently in Git selected author.
   *
   * @return author that is  currently selected in git
   */
  public GitAuthor getUser() {
    StoredConfig config = getJGit().getRepository().getConfig();
    return new GitAuthor(config.getString("user", null, "name"),
        config.getString("user", null, "email")
    );
  }

  /**
   * Method to reinitialize the repository.
   */
  public void reinitialize() {
    initializeRepository();
  }

  /**
   * Returns the merge oommit message as prepared by git.
   *
   * @return String with commit message; NULL if MERGE_MSG does not exist in .git
   */
  public String getMergeCommitMessage() {

    File mergeFile = new File(getRepository().getDirectory(), "MERGE_MSG");
    if (!mergeFile.exists() || mergeFile.isDirectory()) {
      return null;
    }
    try (BufferedReader br = new BufferedReader(new FileReader(mergeFile))){
      return br.lines().collect(Collectors.joining(System.lineSeparator()));

    } catch (IOException e) {
      return null;
    }

  }

  /**
   * Method to initalize the Repoistory.
   */
  private static void initializeRepository() {
    Settings settings = Settings.getInstance();
    File path = settings.getActiveRepositoryPath();
    try {
      FileRepositoryBuilder builder = new FileRepositoryBuilder();
      builder.setMustExist(true);
      if (path.getAbsolutePath().matches(".*/.git$"))
        builder.setGitDir(path);
      else
        builder.setWorkTree(path);
      repository = builder.build();
      git = Git.open(path);
    } catch (IOException e) {
      Logger.getGlobal().warning("Beim Öffnen des Git-Repositorys ist etwas schief gelaufen "
          + ERROR_MESSAGE + e.getMessage());
    }
  }

  /**
   * Method to get the currently selected branch.
   *
   * @return Currently selected branch
   * @throws IOException If something with the directory went wrong
   */
  public GitBranch getSelectedBranch() throws IOException {
    String branchName = git.getRepository().getFullBranch();
    return new GitBranch(branchName);
  }

  /**
   * Get all commits of the current Repository.
   *
   * @return Array of all commits without stashes
   */
  public Iterator<GitCommit> getCommits() throws IOException, GitException {
    try {
      Iterable<RevCommit> allCommits;
      allCommits = git.log().all().call();
      return new CommitIterator(allCommits);
    } catch (NoHeadException e) {
      throw new GitException("Der Head wurde nicht gefunden \n"
          + ERROR_MESSAGE + e.getMessage());
    } catch (GitAPIException e) {
      throw new GitException("Eine nicht genauer spezifizierte Fehlermeldung in Git ist aufgetreten \n"
          + ERROR_MESSAGE + e.getMessage());
    }
  }

  /**
   * Get a page of commits for a branch.
   *
   * @param branch the Branch to get commits for
   * @return Array of all commits without stashes
   */
  Iterator<GitCommit> getCommits(GitBranch branch) throws GitException, IOException {
    try {
      Iterable<RevCommit> allCommits;
      allCommits = git.log().add(repository.resolve(branch.getFullName()))
          .call();
      return new CommitIterator(allCommits);
    } catch (NoHeadException e) {
      throw new GitException("Der Head wurde nicht gefunden \n"
          + ERROR_MESSAGE + e.getMessage());
    } catch (IncorrectObjectTypeException e) {
      throw new GitException("Mit Git ist etwas Schiefgelaufen \n"
          + ERROR_MESSAGE + e.getMessage());
    } catch (AmbiguousObjectException | MissingObjectException e) {
      throw new GitException("Mit den internen Objekten ist etwas schief gelaufen \n"
          + ERROR_MESSAGE + e.getMessage());
    } catch (GitAPIException e) {
      throw new GitException("Mit Git ist etwas nicht genauer spezifiziertes schief gelaufen \n"
          + ERROR_MESSAGE + e.getMessage());
    } catch (NullPointerException e) {
      // Apparently JGIT throws a NPE if a branch does not have a commit to reference.
      // e.g. after initializing a repository.
      return new CommitIterator(null);
    }
  }


  /**
   * Get all stashes of the current Repository.
   *
   * @return A list of all Stashes
   */
  @SuppressWarnings("unused")
  public List<GitStash> getStashes() {
    try {
      StashListCommand stashListCommand = new StashListCommand(repository);
      Collection<RevCommit> listOfStashes = stashListCommand.call();
      List<GitStash> gitStashes = new ArrayList<>();
      for (RevCommit stash : listOfStashes) {
        gitStashes.add(new GitStash(stash));
      }
      return gitStashes;
    } catch (GitAPIException e) {
      Logger.getGlobal().warning(e.getMessage());
      return new ArrayList<>();
    }
  }


  /**
   * Get the current status of the working directory.
   * Holds information about all active files and their current stages
   *
   * @return The singleton status object
   */
  public GitStatus getStatus() {
    return GitStatus.getInstance();
  }

  /**
   * Get a list of all configured remotes.
   *
   * @return A list of all remotes
   */
  public List<GitRemote> getRemotes() {
    try {
      List<RemoteConfig> remotes = git.remoteList().call();
      List<GitRemote> gitRemotes = new ArrayList<>();
      for (RemoteConfig config : remotes) {
        List<URIish> uris = config.getURIs();
        String user = uris.iterator().next().getUser();
        String name = config.getName();
        URIish uri = config.getURIs().iterator().next();
        gitRemotes.add(new GitRemote(uri.toString(), user, name));
      }
      return gitRemotes;
    } catch (GitAPIException e) {
      Logger.getGlobal().warning(e.getMessage());
      return new ArrayList<>() {
      };
    }
  }

  /**
   * Method to get a list of the branches, witch are available in the current repository.
   *
   * @return A list of branches in the repository
   */
  public List<GitBranch> getBranches() throws GitException {
    try {
      List<Ref> branches = git.branchList().call();
      List<GitBranch> gitBranches = new ArrayList<>();
      for (Ref branch : branches) {
        gitBranches.add(new GitBranch(branch));
      }
      return gitBranches;
    } catch (GitAPIException e) {
      throw new GitException("Mit Git ist etwas schief gelaufen"
          + ERROR_MESSAGE + e.getMessage());
    }
  }

  /**
   * Method to get list of branches, which are available in the specific online repository.
   *
   * @param remote Online repository, where the branches come from
   * @return List of branches in the repository
   */
  public List<GitBranch> getBranches(GitRemote remote) throws GitException {
    Collection<Ref> refs;
    List<GitBranch> branches = new ArrayList<>();

    try {
      // TODO: That's super ugly.
      Git.lsRemoteRepository()
              .setHeads(false)
              .setRemote(remote.getUrl())
              .setTransportConfigCallback(CredentialProviderHolder::configureTransport)
              .call();

      refs = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
      for (Ref ref : refs) {
        if (ref.getName().startsWith("refs/remotes/" + remote.getName() + "/") && !ref.getName().endsWith("/HEAD"))
          branches.add(new GitBranch(ref));
      }
    } catch (GitAPIException e) {
      throw new GitException();
    }

    return branches;
  }

}



