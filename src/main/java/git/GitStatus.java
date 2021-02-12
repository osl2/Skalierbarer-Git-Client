package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class holds the current status of the git repo and acts as an adapter class
 * for the JGit Status class.
 * To ensure there is only one status element, this class implements the Singleton pattern
 * TODO: identify methods (below) that are not necessary
 */
public class GitStatus {
  private static GitStatus gitStatus = null;

  /*
   * The constructor is private to ensure there is only one GitStatus object at a time
   */
  private GitStatus() {
  }

  /**
   * Method to get the unique GitStatus object.
   *
   * @return The unique GitStatus object
   */
  public static GitStatus getGitStatus() {
    if (gitStatus == null) {
      gitStatus = new GitStatus();
    }
    return gitStatus;
  }

  /**
   * Jgit: getAdded(); e.g. what you get if you call git add ... on a newly created file.
   * This method returns a list of files that have been newly added to the index.
   *
   * @return a list of files added to the index, not in HEAD
   * @see GitFile
   *     TODO: JGit returns a Set of Strings instead. Modify?
   */
  public List<GitFile> getAddedFiles() throws GitException, IOException {
    try {
      Git git = GitData.getJGit();
      Repository repository = GitData.getRepository();
      Set<String> filesAddedJgit;
      filesAddedJgit = git.status().call().getAdded();
      return toGitFile(filesAddedJgit);
    } catch (GitAPIException e) {
      throw new GitException("Git Status konnte nicht erfolgreich ausgeführt werden,"
          + "\n Fehlernachricht: " + e.getMessage());
    }
  }

  /**
   * Jgit: getChanged(); e.g. what you get if you modify an existing file
   * and call git add ... on it
   * This method returns a list of all files with at least two versions:
   * an older version from a former commit and a modified version in the working directory
   *
   * @return a list of files that have changed from HEAD to index
   */
  public List<GitFile> getChangedFiles() throws IOException, GitException {
    try {
      Git git = GitData.getJGit();
      File gitDir = git.getRepository().getDirectory();
      Set<String> jgitFiles = null;
      jgitFiles = git.status().call().getChanged();
      return toGitFile(jgitFiles);
    } catch (GitAPIException e) {
      throw new GitException("Ein Fehler in Git ist aufgetreten \n"
          + "Fehlermeldung: " + e.getMessage());
    }

  }

  /**
   * Jgit: getModified(); e.g. what you get if you modify an existing file without
   * adding it to the index.
   * This method returns a list of files in the index that have not been
   * added to the staging-area yet
   *
   * @return a list of files modified on disk relative to the index
   */
  public List<GitFile> getModifiedFiles() throws IOException, GitException {
    try {
      Git git = GitData.getJGit();
      Set<String> modifiedJgit = git.status().call().getModified();
      return toGitFile(modifiedJgit);
    } catch (GitAPIException e) {
      throw new GitException("Ein Fehler in Git ist aufgetreten \n"
          + "Fehlermeldung: " + e.getMessage());
    }
  }

  /**
   * Jgit: getUntracked(); e.g. what you get if you create a new file without adding it to the index
   *
   * @return list of files that are not ignored, and not in the index
   */
  public List<GitFile> getUntrackedFiles() throws IOException, GitException {
    try {
      Git git = GitData.getJGit();
      Set<String> untracked = git.status().call().getUntracked();
      return toGitFile(untracked);
    } catch (GitAPIException e) {
      throw new GitException("Ein Fehler in Git ist aufgetreten \n"
          + "Fehlermeldung: " + e.getMessage());
    }

  }

  /**
   * Like getUntrackedFiles(), but with folders.
   *
   * @return Returns a lisst of untracked files
   * @see #getUntrackedFiles()
   */
  public List<String> getUntrackedFolders() throws GitException, IOException {
    try {
      Git git = GitData.getJGit();
      Set<String> untrackedFolders = git.status().call().getUntrackedFolders();
      return new ArrayList<>(untrackedFolders);
    } catch (GitAPIException e) {
      throw new GitException("Ein Fehler in Git ist aufgetreten \n"
          + "Fehlermeldung: " + e.getMessage());
    }
  }

  /**
   * Jgit: getConflicting(); e.g. what you get if you modify a file that was modified
   * by someone else in the meantime
   *
   * @return a list of files that are in conflict
   */
  public List<GitFile> getConflictingFiles() throws GitException, IOException {
    try {
      Git git = GitData.getJGit();
      Set<String> conflicting = git.status().call().getConflicting();
      return toGitFile(conflicting);
    } catch (GitAPIException e) {
      throw new GitException("Ein Fehler in Git ist aufgetreten \n"
          + "Fehlermeldung: " + e.getMessage());
    }

  }

  /**
   * Jgit: getConflictingStageState():Map(String,IndexDiff.StageState)
   * A map from conflicting path to its IndexDiff.StageState.
   */
  public void getConflictingStageState() throws GitException {
    try {
      Git git = GitData.getJGit();
      Map<String, IndexDiff.StageState> conflictingStageState
          = git.status().call().getConflictingStageState();

    } catch (GitAPIException e) {
      throw new GitException("Ein Fehler in Git ist aufgetreten \n"
          + "Fehlermeldung: " + e.getMessage());
    }
    return;
    //TODO: raus?
  }

  /**
   * JGit: get ignored files which are not in the index.
   *
   * @return set of files and folders that are ignored and not in the index
   */
  public List<GitFile> getIgnoredNotInIndex() throws GitException, IOException {
    try {
      Git git = GitData.getJGit();
      Set<String> ignored = git.status().call().getIgnoredNotInIndex();
      return toGitFile(ignored);
    } catch (GitAPIException e) {
      throw new GitException("Ein Fehler in Git ist aufgetreten \n"
          + "Fehlermeldung: " + e.getMessage());
    }
  }

  /**
   * Jgit: getUncommitedChanges(); e.g. all files changed in the index or working tree
   *
   * @return set of files and folders that are known to the repo and changed
   *     either in the index or in the working tree
   */
  public List<GitFile> getUncommittedChanges() throws IOException, GitException {
    try {
      Git git = GitData.getJGit();
      Set<String> changes = git.status().call().getUncommittedChanges();
      return toGitFile(changes);
    } catch (GitAPIException e) {
      throw new GitException("Ein Fehler in Git ist aufgetreten \n"
          + "Fehlermeldung: " + e.getMessage());
    }

  }

  /**
   * This method indicates whether the working state is clean or not.
   *
   * @return true if there are no untracked changes in the working directory
   */
  public boolean isClean() throws GitException {
    Git git = GitData.getJGit();
    try {
      return git.status().call().isClean();
    } catch (GitAPIException e) {
      throw new GitException("Fehler in Git \n"
          + "Fehlermeldung: " + e.getMessage());
    }
  }

  /**
   * This method indicates whether there are changes in the working directory
   * that have not been committed yet.
   *
   * @return true if any tracked file has changed
   */
  public boolean hasUncommittedChanges() throws GitException {
    Git git = GitData.getJGit();
    try {
      return git.status().call().hasUncommittedChanges();
    } catch (GitAPIException e) {
      throw new GitException("Fehler in Git \n"
          + "Fehlermeldung: " + e.getMessage());
    }
  }

  private List<GitFile> toGitFile(Set<String> jgitFiles) throws IOException {
    File repoPath = GitData.getRepository().getDirectory();
    List<GitFile> gitFiles = new ArrayList<>();
    for (String file : jgitFiles) {
      File toGitFile = new File(repoPath, file);
      gitFiles.add(new GitFile(toGitFile.getTotalSpace(), toGitFile));
    }
    return gitFiles;
  }
}
