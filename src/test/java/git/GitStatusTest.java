package git;

import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GitStatusTest extends AbstractGitTest {
    private List<GitFile> untrackedFiles;
    private List<GitFile> addedFiles;
    private List<GitFile> modifiedFiles;
    private List<GitFile> changedFiles;
    private GitStatus gitStatus;

    @Test
    public void getAddedFilesReturnsCorrectPathsTest() throws IOException, GitAPIException, GitException {
        File file1 = new File(repo, "file1");
        File subdir = new File(repo, "subdir");
        FileUtils.forceMkdir(subdir);
        File file2 = new File(subdir, "file1");

        // Create empty files
        new FileOutputStream(file1).close();
        new FileOutputStream(file2).close();

        git.add()
                .addFilepattern(repo.toPath().relativize(file1.toPath()).toString())
                .addFilepattern(repo.toPath().relativize(file2.toPath()).toString())
                .call();

        List<GitFile> addedPaths = GitStatus.getGitStatus().getAddedFiles();

        // Sort according to the order of the wrapped files.
        addedPaths.sort(Comparator.comparing(GitFile::getPath));

        assertEquals(2, addedPaths.size());
        assertEquals(file1.getAbsolutePath(), addedPaths.get(0).getPath().getAbsolutePath());
        assertEquals(file2.getAbsolutePath(), addedPaths.get(1).getPath().getAbsolutePath());
    }

    @Test
    public void untrackedAddedFileStateTest() throws IOException, GitException, GitAPIException {
        File file = new File(repo, "file");
        new FileOutputStream(file).close();
        GitFile gitFile = new GitFile(file.getTotalSpace(), file);
        reloadFileLists();
        assertTrue(untrackedFiles.contains(gitFile));
        assertFalse(addedFiles.contains(gitFile));
        assertFalse(modifiedFiles.contains(gitFile));
        assertFalse(changedFiles.contains(gitFile));

        //after git add, file should be ADDED but not UNTRACKED
        git.add().addFilepattern(repo.toPath().relativize(file.toPath()).toString()).call();

        reloadFileLists();
        assertFalse(untrackedFiles.contains(gitFile));
        assertTrue(addedFiles.contains(gitFile));
        assertFalse(modifiedFiles.contains(gitFile));
        assertFalse(changedFiles.contains(gitFile));

        //perform further changes, gitFile should now be both MODIFIED and ADDED
        FileOutputStream out = new FileOutputStream(file);
        out.write(new String("Test").getBytes(StandardCharsets.UTF_8));

        reloadFileLists();
        assertFalse(untrackedFiles.contains(gitFile));
        assertTrue(addedFiles.contains(gitFile));
        assertTrue(modifiedFiles.contains(gitFile));
        assertFalse(changedFiles.contains(gitFile));

        //call git add and git commit, file should now be in no list
        git.add().addFilepattern(repo.toPath().relativize(file.toPath()).toString()).call();
        git.commit().setMessage("").call();

        reloadFileLists();
        assertFalse(untrackedFiles.contains(gitFile));
        assertFalse(addedFiles.contains(gitFile));
        assertFalse(modifiedFiles.contains(gitFile));
        assertFalse(changedFiles.contains(gitFile));

        //modify and add file, should now be CHANGED
        out.write(new String("TestTest").getBytes(StandardCharsets.UTF_8));
        out.close();
        git.add().addFilepattern(repo.toPath().relativize(file.toPath()).toString()).call();

        reloadFileLists();
        assertFalse(untrackedFiles.contains(gitFile));
        assertFalse(addedFiles.contains(gitFile));
        assertFalse(modifiedFiles.contains(gitFile));
        assertTrue(changedFiles.contains(gitFile));
    }

    private void reloadFileLists() throws IOException, GitException {
        gitStatus = GitStatus.getGitStatus();
        untrackedFiles = gitStatus.getUntrackedFiles();
        addedFiles = gitStatus.getAddedFiles();
        modifiedFiles = gitStatus.getModifiedFiles();
        changedFiles = gitStatus.getChangedFiles();

    }
}
