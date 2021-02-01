package git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import settings.Settings;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Is testing GitData
 */
public class GitDataTest {
    private static final File repo = new File(System.getProperty("java.io.tmpdir"), "testRepo1");
    GitData gitData;
    Git git;
    Repository repository;
    Settings settings;

    @BeforeAll
    public static void setUp() throws GitAPIException, IOException {

        Git.init().setDirectory(repo).setBare(false).call();
        Git git = Git.open(repo);
        // All commits are empty and contain no changes. This needs to change if further testcases check data.
        git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
                .setMessage("Commit 1").call();
        git.commit().setCommitter("Tester 2", "tester2@example.com").setSign(false)
                .setMessage("Commit 2").call();
        git.commit().setCommitter("Tester 3", "tester3@example.com").setSign(false)
                .setMessage("Commit 3").call();
        git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
                .setMessage("Commit 4").call();

        // Delete the Repository when the VM dies.
        repo.deleteOnExit();
        git.close();
    }


    @BeforeEach
    public void init() {
        settings = Settings.getInstance();
        settings.setActiveRepositoryPath(repo);
        gitData = new GitData();
        try {
            git = Git.open(repo);
            repository = git.getRepository();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBranchesTest() throws GitAPIException {
        List<GitBranch> branchesGitData = gitData.getBranches();
        List<Ref> branchesJGit = git.branchList().call();
        assertEquals(branchesGitData.size(), branchesJGit.size());
        // TODO: mehr testen

    }

}

