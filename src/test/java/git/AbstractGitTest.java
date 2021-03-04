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
import java.io.FileWriter;
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

    protected boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete(); // The directory is empty now and can be deleted.
    }

    @BeforeEach
    protected void beforeEach() throws GitAPIException, IOException, GitException, URISyntaxException {
        deleteDir(repo);
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
        File textFile = new File(repo.getPath() + "/textFile.txt");
        FileWriter fr = new FileWriter(textFile, true);
        fr.write("data 1");
        fr.flush();

        git.add().addFilepattern(textFile.getName()).call();
        git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
                .setMessage("Commit 1").call();

        fr.write("data 2");
        fr.flush();

        git.add().addFilepattern(textFile.getName()).call();
        git.commit().setCommitter("Tester 2", "tester2@example.com").setSign(false)
                .setMessage("Commit 2").call();

        fr.write("Neuer Inhalt des Files");
        fr.flush();

        git.add().addFilepattern(textFile.getName()).call();
        git.commit().setCommitter("Tester 3", "tester3@example.com").setSign(false)
                .setMessage("Commit 3").call();
        git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
                .setMessage("Commit 4").call();

        fr.write("Nicht gestaged");
        fr.close();
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
