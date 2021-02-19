package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class holds the current status of the git repo and acts as an adapter class
 * for the JGit Status class.
 * To ensure there is only one status element, this class implements the Singleton pattern
 */
public class GitStatus {

    private static GitStatus gitStatus = null;

    /**
     * The constructor is private to ensure there is only one GitStatus object at a time
     */
    private GitStatus() {
    }

    /**
     * Method to get the unique GitStatus object.
     *
     * @return The unique GitStatus object
     */
    static GitStatus getInstance() {
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
            for (GitFile file : removedFiles){
                file.setStaged(true);
                file.setDeleted(true);
            }
            return removedFiles;
        }
        catch (GitAPIException e){
            throw new GitException("Ein Fehler in Git ist aufgetreten \n"
                    + "Fehlermeldung: " + e.getMessage());
        }
    }

    /**
     * @return A list of files that have been deleted manually, therefore do not appear in the working directory anymore
     * (but are still present in the index)
     * @throws IOException  if the filesystem returns an error
     * @throws GitException if the parsing of the Git data fails
     */
    public List<GitFile> getMissingFiles() throws IOException, GitException {
        try{
            Git git = GitData.getJGit();
            Set<String> missing = git.status().call().getMissing();
            List<GitFile> missingFiles = toGitFile(missing);
            for (GitFile file : missingFiles){
                file.setDeleted(true);
            }
            return missingFiles;
        }
        catch (GitAPIException e){
            throw new GitException("Ein Fehler in Git ist aufgetreten \n"
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
     * Method to get a list of all files, that are new in the repository directory.
     * @return List of all new files
     * @throws IOException If something with the Files went wrong
     * @throws GitException If something with git went wrong
     */
    public List<GitFile> getDeletedFiles() throws IOException, GitException {
        List<GitFile> deletedFiles = getMissingFiles();
        deletedFiles.addAll(getRemovedFiles());
        return deletedFiles;
    }


    /**
     * Method to get a list of all files, that are new in the repository directory.
     * @return List of all new files
     * @throws IOException If something with the Files went wrong
     * @throws GitException If something with git went wrong
     */
    public List<GitFile> getNewFiles() throws IOException, GitException {
        List<GitFile> newFiles = getUntrackedFiles();
        newFiles.addAll(getAddedFiles());
        return newFiles;
    }


    /**
     * Method to get a list of all files, that are modified.
     * @return List of all modified files
     * @throws IOException If something with the Files went wrong
     * @throws GitException If something with git went wrong
     */
    public List<GitFile> getModifiedChangedFiles() throws IOException, GitException {
        List<GitFile> modifiedChangedFiles = getModifiedFiles();
        modifiedChangedFiles.addAll(getChangedFiles());
        return modifiedChangedFiles;
    }

    /**
     * Method to get a list of all files, that are staged.
     * @return List of all staged files
     * @throws IOException If something with the Files went wrong
     * @throws GitException If something with git went wrong
     */
    public List<GitFile> getStagedFiles() throws IOException, GitException {
        List<GitFile> stagedFiles = getAddedFiles();
        stagedFiles.addAll(getChangedFiles());
        stagedFiles.addAll(getRemovedFiles());
        return stagedFiles;
    }

}
