package git;

import commands.Add;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddTest extends AbstractGitTest {
    @Test
    void executeAddTest() throws IOException, GitAPIException, GitException {
        File file1 = new File(repo, "file1");
        File file2 = new File(repo, "file2");
        File file3 = new File(repo, "file3");
        new FileOutputStream(file1).close();
        new FileOutputStream(file2).close();
        new FileOutputStream(file3).close();
        GitFile gitFile1 = new GitFile(file1.getTotalSpace(), file1);
        GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);
        GitFile gitFile3 = new GitFile(file3.getTotalSpace(), file3);

        //prepare add command to add file2 and file3
        Add add = new Add();
        //List<GitFile> filesToBeAdded = new LinkedList<>();
        //List<GitFile> filesToBeRestored = new LinkedList<>();
        List<GitFile> files = new LinkedList<>();

        //this is done internally by add commit view. Selected files that are not staged yet are added to filesToBeAdded,
        //unselected files that are staged are added to filesToBeRestored
        /*
        filesToBeAdded.add(gitFile2);
        filesToBeAdded.add(gitFile3);
        filesToBeRestored.add(gitFile1);
        add.setFilesToBeAdded(filesToBeAdded);
        add.setFilesToBeRestored(filesToBeRestored);

         */
        files.add(gitFile2);
        files.add(gitFile3);
        add.setFiles(files);

        //add file1 and file2 manually
        git.add()
                .addFilepattern(repo.toPath().relativize(file1.toPath()).toString())
                .addFilepattern(repo.toPath().relativize(file2.toPath()).toString())
                .call();
        List<GitFile> addedFiles = GitStatus.getInstance().getAddedFiles();
        assertTrue(addedFiles.contains(gitFile2));

        //execute add
        add.execute();

        //file2 and file3 should now be added, file1 should have been removed from the
        // staging area, list of added files should contain 2 elements
        addedFiles = GitStatus.getInstance().getAddedFiles();
        assertFalse(addedFiles.contains(gitFile1));
        assertTrue(addedFiles.contains(gitFile2));
        assertTrue(addedFiles.contains(gitFile3));
        assertEquals(2, addedFiles.size());


    }

    @Test
    void getFilesStatusChangedTest() throws IOException, GitAPIException, GitException {
        File file1 = new File(repo, "file1");
        File file2 = new File(repo, "file2");
        File file3 = new File(repo, "file3");
        new FileOutputStream(file1).close();
        new FileOutputStream(file2).close();
        new FileOutputStream(file3).close();
        GitFile gitFile1 = new GitFile(file2.getTotalSpace(), file1);
        GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);
        GitFile gitFile3 = new GitFile(file3.getTotalSpace(), file3);

        //prepare add command to add file2 and file3
        Add add = new Add();

        //configure add command with files that have been selected
        List<GitFile> files = new LinkedList<>();
        files.add(gitFile2);
        files.add(gitFile3);
        add.setFiles(files);

        //add file1 and file2 manually
        git.add()
                .addFilepattern(repo.toPath().relativize(file1.toPath()).toString())
                .addFilepattern(repo.toPath().relativize(file2.toPath()).toString())
                .call();
        assertTrue(GitStatus.getInstance().getStagedFiles().contains(gitFile1));
        assertTrue(GitStatus.getInstance().getStagedFiles().contains(gitFile2));


        /*get files whose status changed. Status of a file can have either changed because the file was selected by the
        user and therefore added, or because the file was deselected and therefore unstaged
         */
        assertTrue(add.getFilesStatusChanged().contains(gitFile1));
        assertFalse(add.getFilesStatusChanged().contains(gitFile2));
        assertTrue(add.getFilesStatusChanged().contains(gitFile3));
    }
}
