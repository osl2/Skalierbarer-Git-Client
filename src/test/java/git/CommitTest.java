package git;

import commands.Commit;
import controller.GUIController;
import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;
import views.AddCommitView;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class CommitTest extends AbstractGitTest {
    private File file;
    static GUIControllerTestable guiControllerTestable;
    static MockedStatic<GUIController> mockedController;
    private final String COMMIT_MESSAGE = "Test Commit";
    private final String COMMIT_AMEND_MESSAGE = "Commit amend";
    private Commit commit;

    @BeforeAll
    static void setup() {
        guiControllerTestable = new GUIControllerTestable();
        mockedController = mockStatic(GUIController.class);
        mockedController.when(GUIController::getInstance).thenReturn(guiControllerTestable);
        guiControllerTestable.resetTestStatus();

    }

    @AfterAll
    static void closeControllerMock() {
        mockedController.close();
    }

    @BeforeEach
    void setAuthor() throws IOException {
        StoredConfig config = git.getRepository().getConfig();
        config.setString("user", null, "name", "TestUser");
        config.setString("user", null, "email", "tester@example.com");
        config.save();

        commit = new Commit();
        file = new File(repo, "file");

    }

    private void resetRepo() throws IOException, GitAPIException, GitException, URISyntaxException {
        deleteDir(repo);
        FileUtils.forceMkdir(repo);
        Git.init().setDirectory(repo).setBare(false).call();
        Git git = Git.open(repo);
        git.close();

        init();
    }

    private void addEmptyFile(File file) throws IOException, GitAPIException {
        //create empty file
        new FileOutputStream(file).close();

        //add file
        git.add()
                .addFilepattern(repo.toPath().relativize(file.toPath()).toString())
                .call();
        Status status = git.status().call();
        Set<String> addedFiles = status.getAdded();
        assertTrue(addedFiles.contains(file.getName()));
    }

    private void commit() throws GitAPIException {
        git.commit().setMessage(COMMIT_MESSAGE).call();
        Iterator<RevCommit> commitIterator = git.log().call().iterator();
        assertTrue(git.status().call().isClean());
        assertTrue(commitIterator.hasNext());
    }

    /*
    Counts the total number of commits in the commit history
     */
    private int countNumCommits() throws GitAPIException {
        //count the total number of commits
        Iterator<RevCommit> iterator = git.log().call().iterator();
        //get the latest commit
        RevCommit latestCommit = iterator.next();
        int numCommits = 1;
        //count the remaining commits
        while (iterator.hasNext()) {
            numCommits++;
            iterator.next();
        }
        return numCommits;
    }

    /*
     * Returns a list of all files in the given commit
     */
    private List<String> getFilePathsInCommit(RevCommit commit) throws IOException {
        RevTree revTree = commit.getTree();
        List<String> filePaths = new ArrayList<>();
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.reset(revTree);
            while (treeWalk.next()) {
                filePaths.add(treeWalk.getPathString());
            }
        }
        return filePaths;
    }

    /*
     * Create a new Commit instance, set amend and commit message.
     * Check whether the CL output equals the respective git command
     */
    @Test
    void getCommandLineTest() {
        //create a new Commit instance and set the commit message
        commit.setCommitMessage(COMMIT_MESSAGE);
        String commandLine = commit.getCommandLine();
        assertEquals(0, commandLine.compareTo("git commit -m\"" + COMMIT_MESSAGE + "\""));

        //set amend flag to true
        commit.setAmend(true);
        commandLine = commit.getCommandLine();
        assertEquals(0, commandLine.compareTo("git commit --amend -m\"" + COMMIT_MESSAGE + "\""));

    }

    @Test
    void getNameTest() {
        assertNull(commit.getName());
    }

    @Test
    void getDescriptionTest() {
        assertNotNull(commit.getDescription());
    }

    @Test
    void executeTest() throws IOException, GitAPIException, GitException, URISyntaxException {
        //reset the repo and add a commit with one file
        resetRepo();
        addEmptyFile(file);
        commit.setCommitMessage(COMMIT_MESSAGE);

        //command should be executed successfully
        assertTrue(commit.execute());

        //file should no longer be added and status should be clean
        Status status = git.status().call();
        Set<String> addedFiles = status.getAdded();
        assertFalse(addedFiles.contains(file.getName()));
        assertTrue(status.isClean());

        assertEquals(1, countNumCommits());

        //get the latest commit. Commit message should equal COMMIT_MESSAGE
        RevCommit latestCommit = git.log().call().iterator().next();
        assertEquals(0, latestCommit.getFullMessage().compareTo(COMMIT_MESSAGE));

        //get a list of files in that latest commit
        List<String> filePathsInCommit = getFilePathsInCommit(latestCommit);
        //the latest commit should contain exactly one file that matches the given path
        assertEquals(1, filePathsInCommit.size());
        assertEquals(0, filePathsInCommit.get(0).compareTo(file.getName()));
    }

    @Test
    void emptyCommitShouldFail() {
        commit.setCommitMessage(COMMIT_MESSAGE);
        assertFalse(commit.execute());
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    @Test
    void amendEmptyCommitHistoryShouldFail() throws URISyntaxException, GitAPIException, GitException, IOException {
        resetRepo();
        commit.setCommitMessage(COMMIT_MESSAGE);
        commit.setAmend(true);
        assertFalse(commit.execute());
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    @Test
    void emptyCommitAmendShouldSucceed() throws IOException, GitAPIException {
        addEmptyFile(file);
        commit();

        //perform empty commit
        Commit amendCommit = new Commit();
        amendCommit.setCommitMessage(COMMIT_AMEND_MESSAGE);
        amendCommit.setAmend(true);
        assertTrue(amendCommit.execute());
    }

    @Test
    void noCommitMessageShouldFail() throws IOException, GitAPIException {
        addEmptyFile(file);
        assertFalse(commit.execute());
    }

    @Test
    void defaultCommitMessageShouldFail() throws IOException, GitAPIException {
        addEmptyFile(file);
        commit.setCommitMessage(AddCommitView.DEFAULT_COMMIT_MESSAGE);
        assertFalse(commit.execute());
    }

    @Test
    void emptyCommitMessageShouldFail() throws IOException, GitAPIException {
        addEmptyFile(file);
        commit.setCommitMessage("");
        assertFalse(commit.execute());
    }

    @Test
    void commitAmendTest() throws URISyntaxException, GitAPIException, GitException, IOException {
        resetRepo();

        File file1 = new File(repo, "file1");
        addEmptyFile(file1);
        commit();

        //add another file and prepare the commit to amend the first one
        File file2 = new File(repo, "file2");
        addEmptyFile(file2);
        Commit amendCommit = new Commit();
        amendCommit.setCommitMessage(COMMIT_AMEND_MESSAGE);
        amendCommit.setAmend(true);
        assertTrue(amendCommit.execute());

        //there should be only one commit in the commit history
        assertEquals(1, countNumCommits());
        //get the latest commit from the commit history
        RevCommit latestCommit = git.log().call().iterator().next();
        //commit message should equal the updated message
        assertEquals(0, latestCommit.getFullMessage().compareTo(COMMIT_AMEND_MESSAGE));

        //the latest commit should contain two files with the correct paths
        List<String> filePaths = getFilePathsInCommit(latestCommit);
        assertEquals(2, filePaths.size());
        assertEquals(0, filePaths.get(0).compareTo(file1.getName()));
        assertEquals(0, filePaths.get(1).compareTo(file2.getName()));

    }

    @Test
    void commitAmendTestDifferentMessage() throws GitAPIException, IOException, GitException, URISyntaxException {
        resetRepo();

        addEmptyFile(file);
        commit();

        //initialize a second commit instance with a new message
        Commit commit2 = new Commit();
        commit2.setCommitMessage(COMMIT_AMEND_MESSAGE);
        commit2.setAmend(true);
        assertTrue(commit2.execute());

        //get the most recent commit
        RevCommit latestCommit = git.log().call().iterator().next();
        //commit message should have been updated
        assertEquals(0, latestCommit.getFullMessage().compareTo(COMMIT_AMEND_MESSAGE));
    }

    @Test
    void commitAmendTestModifiedFile() throws GitAPIException, IOException, GitException, URISyntaxException {
        resetRepo();

        addEmptyFile(file);
        commit();

        //modify file and call git add
        FileOutputStream out = new FileOutputStream(file);
        out.write(("Test").getBytes(StandardCharsets.UTF_8));
        out.close();
        git.add()
                .addFilepattern(repo.toPath().relativize(file.toPath()).toString())
                .call();
        assertFalse(git.status().call().isClean());

        //initialize a commit instance and set amend to true
        Commit amendCommit = new Commit();
        amendCommit.setCommitMessage(COMMIT_AMEND_MESSAGE);
        amendCommit.setAmend(true);
        assertTrue(amendCommit.execute());

        //get the most recent commit. There should only be one commit in the history
        RevCommit latestCommit = git.log().call().iterator().next();
        assertEquals(1, countNumCommits());

        //the commit should contain one file with the correct name
        List<String> pathList = getFilePathsInCommit(latestCommit);
        assertEquals(1, pathList.size());
        String path = pathList.get(0);
        assertEquals(0, path.compareTo(file.getName()));

        //compare content of the committed file. It should equal the updated file's content
        String separator = File.separator.compareTo("\\") == 0 ? "\\" : "/";
        String absolutePath = repo + separator + path;
        File committedFile = new File(absolutePath);
        assertTrue(committedFile.exists());
        BufferedReader reader = new BufferedReader(new FileReader(committedFile));
        StringBuilder content = new StringBuilder();
        String input;
        while ((input = reader.readLine()) != null) {
            content.append(input);
        }
        reader.close();
        assertEquals(0, content.toString().compareTo("Test"));
    }


}
