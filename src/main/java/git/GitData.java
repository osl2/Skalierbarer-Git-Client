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
import org.eclipse.jgit.api.ListBranchCommand;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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
    git.getRepository().close();
    git.close();
    initializeRepository();
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
      String separator = Pattern.quote(System.getProperty("file.separator"));
      String regex = ".*" + separator + ".git";
      if (path.getAbsolutePath().matches(regex))
        builder.setGitDir(path);
      else
        builder.setWorkTree(path);
      repository = builder.build();
      git = Git.open(path);

      git.getRepository().getConfig().setBoolean("commit", null, "gpgsign", false);
    } catch (IOException e) {
      Logger.getGlobal().warning("Beim Öffnen des Git-Repositorys ist etwas schief gelaufen "
              + ERROR_MESSAGE + e.getMessage());
    }
  }

  /**
   * Returns the stored commit message as prepared by git.
   *
   * @return String with commit message; NULL if COMMIT_EDITMSG does not exist in .git
   */
  public String getStoredCommitMessage() {
    try {
      return git.getRepository().readCommitEditMsg();
    } catch (IOException e) {
      Logger.getGlobal().warning("Could not read Commit-Edit-Message");
      return null;
    }
  }

  /**
   * Returns the MERGE_MSG as prepared by commands triggering a git-merge
   *
   * @return String with commit message; NULL if MERGE_MSG does not exist in .git
   */
  public String getMergeMessage() {
    try {
      return git.getRepository().readMergeCommitMsg();
    } catch (IOException e) {
      Logger.getGlobal().warning("Could not read Merge-Message");
      return null;
    }
  }

  /**
   * Allows to write a message to .git/COMMIT_EDIT_MESSAGE i.e. to preserve a commit message after aborting
   *
   * @param message the message to be saved.
   */
  public void writeStoredCommitMessage(String message) {
    try {
      git.getRepository().writeCommitEditMsg(message);
    } catch (IOException e) {
      Logger.getGlobal().warning("Could not store Commit-Edit-Message");
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
   * @throws IOException    if the commit objects could not be read from disk
   * @throws GitException   if the data was malformed or other git errors occured
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
   * @throws GitException if the branches could not be read
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
   * @throws GitException when obtaining the branches fails due to git.
   */
  public List<GitBranch> getBranches(GitRemote remote) throws GitException {
    Collection<Ref> refs;
    List<GitBranch> branches = new ArrayList<>();

    try {
      git.fetch()
              .setRemote(remote.getName())
              .setRefSpecs("refs/heads/*:refs/remotes/"
                      + remote.getName() + "/*")
              .setThin(true)
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



