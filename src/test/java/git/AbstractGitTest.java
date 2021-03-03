package git;

import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import settings.Settings;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public abstract class AbstractGitTest {
    private static final String[][] COMMIT_DATA = {
            new String[]{"Tester 1", "tester1@example.com", "Commit 1"},
            new String[]{"Tester 2", "tester2@example.com", "Commit 2"},
            new String[]{"Tester 3", "tester3@example.com", "Commit 3"},
            new String[]{"Tester 4", "tester4@example.com", "Commit 4"},
    };
    @TempDir
    protected static File repo;
    protected GitData gitData;
    protected Git git;
    protected Repository repository;
    protected Settings settings;

    @BeforeEach
    protected void beforeEach() throws GitAPIException, IOException, GitException, URISyntaxException {
        FileUtils.deleteDirectory(repo);
        FileUtils.forceMkdir(repo);
        setupRepo();

        init();
    }

    @AfterEach
    void tearDown() {
        git.close();
    }

    protected void setupRepo() throws GitAPIException, IOException {
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

    public void init() throws IOException, GitAPIException, GitException, URISyntaxException {
        settings = Settings.getInstance();
        settings.setActiveRepositoryPath(repo);
        gitData = new GitData();
        gitData.reinitialize();
        git = Git.open(repo);
        // Make sure user supplied signing settings dont crash the tests
        git.getRepository().getConfig().setBoolean("commit", null, "gpgsign", false);
        git.getRepository().getConfig().save();
        repository = git.getRepository();

    }

}
