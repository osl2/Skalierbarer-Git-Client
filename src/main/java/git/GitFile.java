package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import settings.Settings;

import java.io.File;
import java.util.Objects;

public class GitFile {
    private long size;
    private File path;
    private boolean ignored;
    private boolean staged = false;
    private boolean deleted;

    GitFile(long size, File path) {
        if (!path.getAbsolutePath().startsWith(Settings.getInstance().getActiveRepositoryPath().getAbsolutePath())) {
            throw new AssertionError("File is not located in the repository directory!");
        }
        this.size = size;
        this.path = path;
        this.staged = false;
    }

    /**
     * Method to get the size of the file
     *
     * @return Size of the file
     */
    public long getSize() {
        return this.size;
    }

    public File getPath() {
        return this.path;
    }

    private String getRelativePath() {
        return GitData.getRepository().getWorkTree().toPath().relativize(path.toPath()).toString();
    }

    /**
     * Adds or removes the file from the .gitignore
     *
     * @param ignored Whether the file should be added to the .gitignore or be removed from it
     */
    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    /**
     * @return true if file has not been added to the index and file
     * path matches a pattern in the .gitignore file
     */
    public boolean isIgnoredNotInIndex() {
        return ignored;
    }

    /**
     * Sets the internal state 'staged' to true if the file is in the staging area (added or changed),
     * false otherwise. Called by GitStatus when GitFile is created. This method is necessary for git diff
     * @param staged Whether the file is in the staging-area
     * @see GitCommit#getDiff(GitFile)
     */
    public void setStaged(boolean staged){
        this.staged = staged;
    }

    /**
     *
     * @return True if the file is in the staging-area, false otherwise
     */
    public boolean isStaged(){
        return staged;
    }

    /**
     * Sets the internal state of the file to 'deleted' if its nested File object does not exist
     * @param deleted Whether the file has been deleted
     */
    public void setDeleted(boolean deleted){
        this.deleted = deleted;
    }

    /**
     * Determinates if the file is deleted
     * @return True if the file does not exist in the workspace anymore
     */
    public boolean isDeleted(){
        return deleted;
    }

    /**
     * Adds the file to the staging-area, thereby performing git add
     *
     * @return True if the file was added to the staging area successfully
     */
    public boolean add() throws GitException {
        Git git = GitData.getJGit();
        try {
            git.add().addFilepattern(this.getRelativePath()).call();
            return true;
        } catch (GitAPIException e) {
            throw new GitException("File not found in Git");

        }
    }


    /**
     * Adds a remove operation for that file to the staging area.
     *
     * @return True if the file was successfully marked as removed.
     * @throws GitException
     */
    public boolean rm() throws GitException {
        Git git = GitData.getJGit();
        try {
            git.rm().addFilepattern(this.getRelativePath()).call();
        } catch (GitAPIException e) {
            throw new GitException("File could not be removed in Git");
        }
        return true;
    }

    /**
     * Removes file from the staging-area, thereby performing git restore --staged <file>
     *
     * @return True if the file was removed from the staging area successfully
     */
    public boolean addUndo() throws GitException {
        Git git = GitData.getJGit();
        try {
            git.reset().addPath(this.getRelativePath()).call();
            return true;
        } catch (GitAPIException e) {
            throw new GitException("File not found in Git");

        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitFile gitFile = (GitFile) o;
        return Objects.equals(path, gitFile.path);
    }


    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
