package git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import settings.Settings;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Is testing GitData
 */
public class GitDataTest {
    private static final String[][] COMMIT_DATA = {
            new String[]{"Tester 1", "tester1@example.com", "Commit 1"},
            new String[]{"Tester 2", "tester2@example.com", "Commit 2"},
            new String[]{"Tester 3", "tester3@example.com", "Commit 3"},
            new String[]{"Tester 4", "tester4@example.com", "Commit 4"},
    };
    @TempDir
    static File repo;
    GitData gitData;
    Git git;
    Repository repository;
    Settings settings;

    @BeforeAll
    public static void setUp() throws GitAPIException, IOException {

        Git.init().setDirectory(repo).setBare(false).call();
        Git git = Git.open(repo);
        // All commits are empty and contain no changes. This needs to change if further testcases check data.

        for (String[] data : COMMIT_DATA) {
            git.commit()
                    .setCommitter(data[0], data[1])
                    .setSign(false)
                    .setMessage(data[2])
                    .call();
        }
        git.close();
    }


    @BeforeEach
    public void init() throws IOException {
        settings = Settings.getInstance();
        settings.setActiveRepositoryPath(repo);
        gitData = new GitData();
        git = Git.open(repo);
        repository = git.getRepository();
    }

    @Test
    public void getBranchesTest() throws GitAPIException {
        List<GitBranch> branchesGitData = gitData.getBranches();
        List<Ref> branchesJGit = git.branchList().call();
        assertEquals(branchesGitData.size(), branchesJGit.size());
        // TODO: mehr testen

    }

    @Test
    public void commitsAreSortedFromNewestToOldestTest() {
        assertEquals(COMMIT_DATA.length, gitData.getCommits().size());
        for (int i = COMMIT_DATA.length - 1; i >= 0; i--) {
            assertEquals(new GitAuthor(COMMIT_DATA[i][0], COMMIT_DATA[i][1]),
                    gitData.getCommits().get(COMMIT_DATA.length - i - 1).getAuthor());
        }

    }

    @Test
    public void commitsHaveCorrectCommitMessageTest() {
        for (int i = COMMIT_DATA.length - 1; i >= 0; i--) {
            assertEquals(COMMIT_DATA[i][2],
                    gitData.getCommits().get(COMMIT_DATA.length - i - 1).getMessage());
        }
    }
}

