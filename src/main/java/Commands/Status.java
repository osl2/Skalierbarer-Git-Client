package Commands;

import Git.GitBlob;

import java.util.List;

public class Status implements ICommand {

    /**
     * Jgit: getAdded(); e.g. what you get if you call git add ... on a newly created file.
     * This method returns a list of files that have been newly added to the index.
     * @return a list of files added to the index, not in HEAD
     * @see Git.GitBlob
     * TODO: JGit returns a Set of Strings instead. Modify?
     */
    public List<GitBlob> getAddedFiles(){
        return null;
    }

    /**
     * Jgit: getChanged(); e.g. what you get if you modify an existing file and call git add ... on it
     * This method returns a list of all files with at least two versions: an older version from a former commit and
     * a modified version in the working directory
     * @return a list of files that have changed from HEAD to index
     */
    public List<GitBlob> getChangedFiles(){
        return null;
    }

    /**
     * Jgit: getModified(); e.g. what you get if you modify an existing file without adding it to the index
     * This method returns a list of files in the index that have not been added to the staging-area yet
     * @return a list of files modified on disk relative to the index
     */
    public List<GitBlob> getModifiedFiles(){
        return null;
    }

    /**
     * Jgit: getUntracked(); e.g. what you get if you create a new file without adding it to the index
     * @return list of files that are not ignored, and not in the index
     */
    public List<GitBlob> getUntrackedFiles(){
        return null;
    }

    /**
     * Like getUntrackedFiles(), but with folders
     * @return
     * @see #getUntrackedFiles()
     */
    public List<String> getUntrackedFolders(){
        return null;
    }

    /**
     * Jgit: getConflicting(); e.g. what you get if you modify a file that was modified by someone else in the meantime
     * @return a list of files that are in conflict
     */
    public List<GitBlob> getConflictingFiles(){
        return null;
    }

    /**
     * Jgit: getConflictingStageState():Map<String,IndexDiff.StageState>
     * A map from conflicting path to its IndexDiff.StageState
     * TODO: data type?
     */
    public void getConflictingStageState(){

    }

    /**
     * JGit: get ignored files which are not in the index
     * @return set of files and folders that are ignored and not in the index
     */
    public List<String> getIgnoredNotInIndex(){
        return null;
    }

    /**
     * Jgit: getUncommitedChanges(); e.g. all files changed in the index or working tree
     * @return set of files and folders that are known to the repo and changed either in the index or in the working tree
     */
    public List<GitBlob> getUncommittedChanges(){
        return null;
    }

    /**
     * This method indicates whether the working state is clean or not
     * @return true if there are no untracked changes in the working directory
     */
    public boolean isClean(){
        return false;
    }

    /**
     * This method indicates whether there are changes in the working directory that have not been committed yet
     * @return true if any tracked file has changed
     */
    public boolean hasUncommittedChanges(){
        return false;
    }

    /**
     * Method to execute the command
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
