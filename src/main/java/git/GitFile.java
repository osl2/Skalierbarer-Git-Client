package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import settings.Settings;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class GitFile {
    private long size;
    private File path;
    private boolean added;
    private boolean changed;
    private boolean modified;
    private boolean untracked;
    private boolean ignored;

    GitFile(long size, File path) {
        if (!path.getAbsolutePath().startsWith(Settings.getInstance().getActiveRepositoryPath().getAbsolutePath())) {
            throw new AssertionError("File is not located in the repository directory!");
        }
        this.size = size;
        this.path = path;
    }

    /**
     * Method to get the size of the file
     *
     * @return Size of the file
     */
    public long getSize() {
        return this.size;
    }

    public File getPath(){
        return this.path;
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
     *
     * @param untracked Whether there is no former version of the file in the index
     */
    public void setUntracked(boolean untracked){
        this.untracked = untracked;
    }

    /**
     * @return true if file is not being tracked, i.e. git add has never been called on this file
     */
    public boolean isUntracked() {
        return untracked;
    }

    /**
     *
     * @param added Wether there is no former version of the file in the index and the file has been
     *              added to the staging-area
     */
    public void setAdded(boolean added){
        this.added = added;
    }

    /**
     *
     * @return true if file has been newly created and has been added to the staging-area
     */
    public boolean isAdded() {
        return false;
    }

    /**
     *
     * @param modified Whether there is a former version of the file and the file has been changed since
     */
    public void setModified(boolean modified){
        this.modified = modified;
    }

    /**
     *
     * @return true if file is being tracked and there is a modified version in the
     * working directory which has not been added to the staging-area
     */
    public boolean isModified() {
        return modified;
    }

    /**
     *
     * @param changed Whether there is a former version of the file and a new version of the file has
     *                been added to the staging area
     */
    public void setChanged(boolean changed){
        this.changed = changed;
    }

    /**
     *
     * @return true if file has been modified and has been added to the staging-area
     */
    public boolean isChanged() {
        return false;
    }

    /**
     *
     * @return True if the file is in the staging-area, false otherwise
     */
    public boolean isStaged(){
        return isAdded() || isChanged();
    }

    /**
     * Adds the file to the staging-area, thereby performing git add
     *
     * @return True if the file was added to the staging area successfully
     */
    public boolean add() throws GitException {
        Repository repository = GitData.getRepository();
        Git git = GitData.getJGit();
        Path repoPath = Paths.get(repository.getDirectory().toURI());
        Path filePath = Paths.get(this.path.toURI());
        String relative = repoPath.relativize(filePath).toString();
        if (relative.substring(0, 3).equals((".." + File.separatorChar).substring(0, 3))) {
            relative = relative.substring(3);
        } else {
            throw new GitException("something with the Filepath went wrong");
        }
        try {
            git.add().addFilepattern(relative).call();
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
        return size == gitFile.size && Objects.equals(path, gitFile.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, path);
    }
}
