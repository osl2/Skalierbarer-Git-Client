package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import settings.Settings;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;

public class GitFile {
    private int size;
    private File path;
    /* TODO: SOME WAY TO TRACK CHANGES */

    GitFile(int size, File path) throws IOException {
        if (!path.getAbsolutePath().startsWith(Settings.getInstance().getActiveRepositoryPath().getAbsolutePath())) {
            throw new IOException("File is not located in the repository directory!");
        }
        this.size = size;
        this.path = path;
    }

    /**
     * Method to get the size of the file
     *
     * @return Size of the file
     */
    public int getSize() {
        return this.size;
    }

    public File getPath(){
        return this.path;
    }

    /**
     * Method to get, if this file is added to the git repository.
     *
     * @return true if file has not been added to the index and file
     * path matches a pattern in the .gitignore file
     */
    public boolean isIgnoredNotInIndex() {
        return false;
    }

    /**
     * This command returns only files that have been newly
     * created and of whom there is no former version in the index.
     *
     * @return true if file is not being tracked, i.e. git add has never been called on this file
     */
    public boolean isUntracked() {
        return false;
    }

    /**
     * This command returns only files that have been newly created and of whom
     * there is no former version in the index.
     *
     * @return true if file has been newly created and has been added to the staging-area
     */
    public boolean isAdded() {
        return false;
    }

    /**
     * This command returns only files of whom an older version is already in the index.
     *
     * @return true if file is being tracked and there is a modified version in the
     * working directory which has not been added to the staging-area
     */
    public boolean isModified() {
        return false;
    }

    /**
     * This command returns only files of whom an older version is already in the index.
     *
     * @return true if file has been modified and has been added to the staging-area
     */
    public boolean isChanged() {
        return false;
    }

    /**
     * Adds the file to the staging-area, thereby performing git add
     *
     * @return True if the file was added to the staging area successfully
     */
    public boolean add() throws GitException {
        GitData gitData = new GitData();
        Repository repository = GitData.getRepository();
        Git git = gitData.getJGit();
        String pathPath = path.getAbsolutePath();
        String base = repository.getDirectory().getAbsolutePath();
        String relative = new File(base).toURI().relativize(new File(pathPath).toURI()).getPath();
        try {
            git.add().addFilepattern(relative).call();
            return true;
        } catch (GitAPIException e) {
            throw new GitException("File not found in Git");

        }
    }

    /**
     * Removes file from the staging-area, thereby performing git restore --staged <file>
     *
     * @return True if the file was removed from the staging area successfully
     */
    public boolean addUndo() {
        return false;
    }

    /**
     * Adds or removes the file from the .gitignore
     *
     * @param ignored Whether the file should be added to the .gitignore or be removed from it
     * @return True if command has been executed successfully
     */
    public boolean setIgnored(boolean ignored) {
        return false;
    }

}
