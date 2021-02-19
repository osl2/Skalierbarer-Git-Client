package git;

import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
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

        List<GitFile> addedPaths = GitStatus.getInstance().getAddedFiles();

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

    @Test
    public void missingRemovedDeletedFilesTest() throws IOException, GitAPIException, GitException {

        //create new file, add and commit it
        File file = new File(repo, "file");
        new FileOutputStream(file).close();
        GitFile gitFile = new GitFile(file.getTotalSpace(), file);
        git.add().addFilepattern(repo.toPath().relativize(file.toPath()).toString()).call();
        git.commit().setMessage("Test").call();

        //delete the file. File should now be missing and deleted, but not removed
        file.delete();
        Status status = git.status().call();
        GitStatus gitStatus = GitStatus.getInstance();
        assertTrue(status.getMissing().contains(file.getName()));
        assertTrue(gitStatus.getMissingFiles().contains(gitFile));
        assertTrue(gitStatus.getDeletedFiles().contains(gitFile));
        assertFalse(gitStatus.getRemovedFiles().contains(gitFile));
        assertEquals(gitStatus.getDeletedFiles().size(),
                gitStatus.getRemovedFiles().size() + gitStatus.getMissingFiles().size());

        //make another file, add and commit it and then call git rm. File should be in getRemoved, but not in getMissing
        File file2 = new File(repo,"file2");
        new FileOutputStream(file2).close();
        GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);
        git.add().addFilepattern(repo.toPath().relativize(file2.toPath()).toString()).call();
        git.commit().setMessage("Test").call();
        git.rm().addFilepattern(repo.toPath().relativize(file2.toPath()).toString()).call();

        status = git.status().call();
        assertFalse(status.getMissing().contains(file2.getName()));
        assertTrue(status.getRemoved().contains(file2.getName()));
        assertFalse(gitStatus.getMissingFiles().contains(gitFile2));
        assertTrue(gitStatus.getDeletedFiles().contains(gitFile2));
        assertTrue(gitStatus.getRemovedFiles().contains(gitFile2));
        assertEquals(gitStatus.getDeletedFiles().size(),
                gitStatus.getRemovedFiles().size() + gitStatus.getMissingFiles().size());
    }

   

    @Test
    public void deletedFilesTest() throws IOException, GitAPIException, GitException {
        //make two files, add and commit them, delete one, remove the other
        File file1 = new File(repo, "file1");
        File file2 = new File(repo, "file2");
        new FileOutputStream(file1).close();
        new FileOutputStream(file2).close();
        GitFile gitFile1 = new GitFile(file1.getTotalSpace(), file1);
        GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);

        git.add().addFilepattern(repo.toPath().relativize(file1.toPath()).toString()).call();
        git.add().addFilepattern(repo.toPath().relativize(file2.toPath()).toString()).call();
        git.commit().setMessage("Test").call();

        git.rm().addFilepattern(repo.toPath().relativize(file1.toPath()).toString()).call();
        file2.delete();

        //get status from JGit and GitStatus instance
        Status status = git.status().call();
        GitStatus gitStatus = GitStatus.getInstance();

        //file1 should be removed, since we called git rm on it
        assertTrue(status.getRemoved().contains(file1.getName()));
        assertTrue(gitStatus.getRemovedFiles().contains(gitFile1));

        //file 2 was deleted manually, therefore it should only be missing
        assertTrue(status.getMissing().contains(file2.getName()));
        assertTrue(gitStatus.getMissingFiles().contains(gitFile2));

        //deleted files from GitStatus should contain both files and have the same size as getMissing and getDeleted together
        assertTrue(gitStatus.getDeletedFiles().contains(gitFile1));
        assertTrue(gitStatus.getDeletedFiles().contains(gitFile2));
        assertEquals(gitStatus.getDeletedFiles().size(),
                gitStatus.getRemovedFiles().size() + gitStatus.getMissingFiles().size());
    }

    @Test
    public void newFilesTest() throws IOException, GitException, GitAPIException {
        //make two files, add one of them
        File file1 = new File(repo, "file1");
        File file2 = new File(repo, "file2");
        new FileOutputStream(file1).close();
        new FileOutputStream(file2).close();
        GitFile gitFile1 = new GitFile(file1.getTotalSpace(), file1);
        GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);

        git.add().addFilepattern(repo.toPath().relativize(file1.toPath()).toString()).call();

        //file1 should now be added, file 2 should be untracked
        Status status = git.status().call();
        GitStatus gitStatus = GitStatus.getInstance();

        assertTrue(status.getAdded().contains(file1.getName()));
        assertTrue(gitStatus.getAddedFiles().contains(gitFile1));

        assertTrue(status.getUntracked().contains(file2.getName()));
        assertTrue(gitStatus.getUntrackedFiles().contains(gitFile2));

        //newFiles should contain both files and have the same size as getAdded + getUntracked
        assertTrue(gitStatus.getNewFiles().contains(gitFile1));
        assertTrue(gitStatus.getNewFiles().contains(gitFile2));
        assertEquals(gitStatus.getNewFiles().size(),
                gitStatus.getAddedFiles().size() + gitStatus.getUntrackedFiles().size());

    }

    @Test
    public void modifiedFilesTest() throws IOException, GitAPIException, GitException {
        //make two files, add and commit them
        File file1 = new File(repo, "file1");
        File file2 = new File(repo, "file2");
        new FileOutputStream(file1).close();
        new FileOutputStream(file2).close();
        GitFile gitFile1 = new GitFile(file1.getTotalSpace(), file1);
        GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);

        git.add().addFilepattern(repo.toPath().relativize(file1.toPath()).toString()).call();
        git.add().addFilepattern(repo.toPath().relativize(file2.toPath()).toString()).call();
        git.commit().setMessage("Test").call();

        //modify both files, add file1
        String change = "Test";
        FileOutputStream out1 = new FileOutputStream(file1);
        FileOutputStream out2 = new FileOutputStream(file2);
        out1.write(change.getBytes(StandardCharsets.UTF_8));
        out2.write(change.getBytes(StandardCharsets.UTF_8));
        out1.close();
        out2.close();

        git.add().addFilepattern(repo.toPath().relativize(file1.toPath()).toString()).call();

        //file1 should now be changed, file 2 should be modified
        Status status = git.status().call();
        GitStatus gitStatus = GitStatus.getInstance();

        assertTrue(status.getChanged().contains(file1.getName()));
        assertTrue(gitStatus.getChangedFiles().contains(gitFile1));

        assertTrue(status.getModified().contains(file2.getName()));
        assertTrue(gitStatus.getModifiedFiles().contains(gitFile2));

        //newFiles should contain both files and have the same size as getAdded + getUntracked
        assertTrue(gitStatus.getModifiedChangedFiles().contains(gitFile1));
        assertTrue(gitStatus.getModifiedChangedFiles().contains(gitFile2));
        assertEquals(gitStatus.getModifiedChangedFiles().size(),
                gitStatus.getChangedFiles().size() + gitStatus.getModifiedFiles().size());
    }

    @Test
    public void getStagedFilesTest() throws IOException, GitAPIException, GitException {
        //make two files, add and commit file1
        File file1 = new File(repo, "file1");
        File file2 = new File(repo, "file2");
        new FileOutputStream(file1).close();
        new FileOutputStream(file2).close();
        GitFile gitFile1 = new GitFile(file1.getTotalSpace(), file1);
        GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);

        git.add().addFilepattern(repo.toPath().relativize(file1.toPath()).toString()).call();
        git.commit().setMessage("Test").call();

        //modify file1
        FileOutputStream out  = new FileOutputStream(file1);
        out.write(new String("Test").getBytes(StandardCharsets.UTF_8));
        out.close();

        //add file1
        git.add().addFilepattern(repo.toPath().relativize(file1.toPath()).toString()).call();

        //add file2
        git.add().addFilepattern(repo.toPath().relativize(file2.toPath()).toString()).call();


        //file1 should now be in getChanged, file2 should be in getAdded, both should be in getStaged
        Status status = git.status().call();
        GitStatus gitStatus = GitStatus.getInstance();
        assertTrue(status.getChanged().contains(file1.getName()));
        assertTrue(status.getAdded().contains(file2.getName()));
        assertTrue(gitStatus.getChangedFiles().contains(gitFile1));
        assertTrue(gitStatus.getAddedFiles().contains(gitFile2));
        assertTrue(gitStatus.getStagedFiles().contains(gitFile1));
        assertTrue(gitStatus.getStagedFiles().contains(gitFile2));
        assertEquals(gitStatus.getStagedFiles().size(),
                gitStatus.getChangedFiles().size() + gitStatus.getAddedFiles().size());
    }


    private void reloadFileLists() throws IOException, GitException {
        gitStatus = GitStatus.getInstance();
        untrackedFiles = gitStatus.getUntrackedFiles();
        addedFiles = gitStatus.getAddedFiles();
        modifiedFiles = gitStatus.getModifiedFiles();
        changedFiles = gitStatus.getChangedFiles();

    }
}
