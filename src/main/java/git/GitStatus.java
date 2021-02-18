package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.IndexDiff;

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
 */
public class GitStatus {
    //TODO: delete methods that are not used?

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
     */
    public List<GitFile> getAddedFiles() throws GitException, IOException {
        try {
            Git git = GitData.getJGit();
            Set<String> filesAddedJgit;
            filesAddedJgit = git.status().call().getAdded();
            List<GitFile> addedGitFiles = toGitFile(filesAddedJgit);
            for (GitFile gitFile : addedGitFiles){
                gitFile.setStaged(true);
            }
            return addedGitFiles;
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
            Set<String> jgitFiles;
            jgitFiles = git.status().call().getChanged();
            List<GitFile> changedGitFiles = toGitFile(jgitFiles);
            for (GitFile gitFile : changedGitFiles){
                gitFile.setStaged(true);
            }
            return changedGitFiles;
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
            List<GitFile> modifiedGitFiles = toGitFile(modifiedJgit);
            return modifiedGitFiles;
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
            List<GitFile> untrackedGitFiles = toGitFile(untracked);
            return untrackedGitFiles;
        } catch (GitAPIException e) {
            throw new GitException("Ein Fehler in Git ist aufgetreten \n"
                    + "Fehlermeldung: " + e.getMessage());
        }

    }

    /**
     * @return A list of files that are known to the index but have been removed and added to the staging area (what you get
     * if you call git rm on an existing file or if you delete the file manually and call git add afterwards=
     * @throws IOException Thrown by toGitFile
     * @throws GitException If the status could not be obtained from JGit
     */
    public List<GitFile> getRemovedFiles() throws IOException, GitException {
        try{
            Git git = GitData.getJGit();
            Set<String> removed = git.status().call().getRemoved();
            List<GitFile> removedFiles = toGitFile(removed);
            return removedFiles;
        }
        catch (GitAPIException e){
            throw new GitException("Ein Fehler in Git ist aufgetreten \n"
                    + "Fehlermeldung: " + e.getMessage());
        }
    }

    /**
     *
     * @return A list of files that have been deleted manually, therefore do not appear in the working directory anymore
     * (but are still present in the index)
     * @throws IOException
     * @throws GitException
     */
    public List<GitFile> getMissingFiles() throws IOException, GitException {
        try{
            Git git = GitData.getJGit();
            Set<String> missing = git.status().call().getMissing();
            List<GitFile> missingFiles = toGitFile(missing);
            return missingFiles;
        }
        catch (GitAPIException e){
            throw new GitException("Ein Fehler in Git ist aufgetreten \n"
                    + "Fehlermeldung: " + e.getMessage());
        }
    }

    /**
     * Like getUntrackedFiles(), but with folders.
     *
     * @return Returns a list of untracked files
     * @see #getUntrackedFiles()
     */
    public List<String> getUntrackedFolders() throws GitException {
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
    //TODO: Rückgabetyp
    public void getConflictingStageState() throws GitException {
        try {
            Git git = GitData.getJGit();
            Map<String, IndexDiff.StageState> conflictingStageState
                    = git.status().call().getConflictingStageState();
            // todo implement?
            throw new AssertionError("not implemented");
        } catch (GitAPIException e) {
            throw new GitException("Ein Fehler in Git ist aufgetreten \n"
                    + "Fehlermeldung: " + e.getMessage());
        }
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
            List<GitFile> ignoredGitFiles = toGitFile(ignored);
            for (GitFile gitFile : ignoredGitFiles){
                gitFile.setIgnored(true);
            }
            return ignoredGitFiles;
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
        File repoPath = GitData.getRepository().getWorkTree();
        List<GitFile> gitFiles = new ArrayList<>();
        for (String file : jgitFiles) {
            File toGitFile = new File(repoPath, file);
            gitFiles.add(new GitFile(toGitFile.getTotalSpace(), toGitFile));
        }
        return gitFiles;
    }


    /**
     * Returns all staged files, i.e. added and modified files
     * @return A list of all files in the staging-area
     * @throws GitException Thrown by getAddedFiles(), getChangedFiles()
     * @throws IOException Thrown by getAddedFiles(), getChangedFiles()
     */
    public List<GitFile> getStagedFiles() throws GitException, IOException {
        List<GitFile> stagedFiles = getAddedFiles();
        stagedFiles.addAll(getChangedFiles());
        return stagedFiles;
    }

    public List<GitFile> getDeletedFiles() throws IOException, GitException {
        List<GitFile> deletedFiles = getMissingFiles();
        deletedFiles.addAll(getRemovedFiles());
        return deletedFiles;
    }

    /**
     * Returns all unstaged files, i.e. untracked and modified files
     * @return A list of all files that are not in the staging-area
     * @throws GitException thrown by getUntrackedFiles() and getModifiedFiles()
     * @throws IOException thrown by getUntrackedFiles(), getModifiedFiles()
     */
    public List<GitFile> getUnstagedFiles() throws IOException, GitException {
        List<GitFile> unstagedFiles = getUntrackedFiles();
        unstagedFiles.addAll(getModifiedFiles());
        return unstagedFiles;
    }
}
