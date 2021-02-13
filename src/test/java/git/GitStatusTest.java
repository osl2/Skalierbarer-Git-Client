package git;

import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitStatusTest extends AbstractGitTest {

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
}
