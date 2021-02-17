package git;

import commands.Add;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AddTest extends AbstractGitTest{
    @Test
    public void executeAddTest() throws IOException, GitAPIException, GitException {
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
        List<GitFile> files = new LinkedList<>();
        files.add(gitFile2);
        files.add(gitFile3);
        add.addFiles(files);

        //add file2 manually
        git.add()
                .addFilepattern(repo.toPath().relativize(file2.toPath()).toString())
                .call();

        //execute add
        add.execute();

        //file2 and file3 should now be added, list of added files should contain only 2 elements (not 3)
        List<GitFile> addedFiles = GitStatus.getGitStatus().getAddedFiles();
        assertFalse(addedFiles.contains(file1));
        assertTrue(addedFiles.contains(file2));
        assertTrue(addedFiles.contains(file3));
        assertEquals(2, addedFiles.size());


    }
}
