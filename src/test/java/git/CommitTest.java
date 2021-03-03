package git;

import commands.Commit;
import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommitTest extends AbstractGitTest {
    private File file;

    @BeforeEach
    void setAuthor() throws IOException {
        StoredConfig config = git.getRepository().getConfig();
        config.setString("user", null, "name", "TestUser");
        config.setString("user", null, "email", "123@web.de");
        config.save();

    }

    private void resetRepo() throws IOException, GitAPIException, GitException, URISyntaxException {
        FileUtils.deleteDirectory(repo);
        FileUtils.forceMkdir(repo);
        Git.init().setDirectory(repo).setBare(false).call();
        Git git = Git.open(repo);
        git.close();

        init();
    }

    private void addFile() throws IOException, GitAPIException {
        //create new file
        file = new File(repo, "file");
        new FileOutputStream(file).close();

        //add file
        git.add()
                .addFilepattern(repo.toPath().relativize(file.toPath()).toString())
                .call();
        Status status = git.status().call();
        Set<String> addedFiles = status.getAdded();
        assert addedFiles.contains(file.getName());
    }

    /*
     * Create a new Commit instance, set amend and commit message.
     * Check whether the CL output equals the respective git command
     */
    @Test
    void getCommandLineTest() {
        //create a new Commit instance and set the commit message
        Commit commit = new Commit();
        commit.setCommitMessage("Test message");
        String commandLine = commit.getCommandLine();
        assertEquals(0, commandLine.compareTo("git commit -m\"Test message\""));

        //set amend flag to true
        commit.setAmend(true);
        commandLine = commit.getCommandLine();
        assertEquals(0, commandLine.compareTo("git commit --amend -m\"Test message\""));

    }

    @Test
    void getNameTest() {
        Commit commit = new Commit();
        assertNull(commit.getName());
    }

    @Test
    void getDescriptionTest() {
        Commit commit = new Commit();
        String description = "Erstellt eine neue Einbuchungen mit den Änderungen aus der " +
                "Staging-Area und der angegebenen Commit-Nachricht";
        assertEquals(0, commit.getDescription().compareTo(description));
    }

    @Test
    void executeTest() throws IOException, GitAPIException {
        addFile();
        Commit commit = new Commit();
        commit.setCommitMessage("Test");

        //command should be executed successfully
        assertTrue(commit.execute());

        //file should no longer be added and status should be clean
        Status status = git.status().call();
        Set<String> addedFiles = status.getAdded();
        assertFalse(addedFiles.contains(file.getName()));
        assertTrue(status.isClean());

        RevCommit revCommit = git.log().call().iterator().next();
        assertEquals(0, revCommit.getFullMessage().compareTo("Test"));
        RevTree revTree = revCommit.getTree();
        int count = 0;
        String path = "";
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.reset(revTree);
            while (treeWalk.next()) {
                count++;
                path = treeWalk.getPathString();
            }
        }
        assertEquals(1, count);
        assertEquals(0, path.compareTo(file.getName()));
    }

    @Test
    void emptyCommitShouldFail() {
        Commit commit = new Commit();
        commit.setCommitMessage("Test");
        assertFalse(commit.execute());
    }

    @Test
    void amendEmptyCommitHistoryShouldFail() throws URISyntaxException, GitAPIException, GitException, IOException {
        resetRepo();
        Commit commit = new Commit();
        commit.setCommitMessage("Test");
        commit.setAmend(true);
        assertFalse(commit.execute());
    }

    @Test
    void emptyCommitAmendShouldSucceed() throws IOException, GitAPIException {
        addFile();
        Commit commit1 = new Commit();
        commit1.setCommitMessage("Test");
        assertTrue(commit1.execute());

        //staging area should now be empty
        assertTrue(git.status().call().isClean());

        //perform empty commit
        Commit commit2 = new Commit();
        commit2.setCommitMessage("Amend test");
        commit2.setAmend(true);
        assertTrue(commit2.execute());
    }

    @Test
    void emptyCommitMessageShouldFail() throws IOException, GitAPIException {
        addFile();
        Commit commit = new Commit();
        assertFalse(commit.execute());
    }

    @Test
    void commitAmendTestDifferentMessage() throws GitAPIException, IOException {
        addFile();
        Commit commit1 = new Commit();
        commit1.setCommitMessage("Test message 1");

        //command should be executed successfully
        assertTrue(commit1.execute());

        //initialize a second commit instance with a new message
        Commit commit2 = new Commit();
        commit2.setCommitMessage("Test message 2");
        commit2.setAmend(true);
        assertTrue(commit2.execute());

        //total number of commits should be 1, commit message should have changed
        RevCommit revCommit = git.log().call().iterator().next();
        assertEquals(0, revCommit.getFullMessage().compareTo("Test message 2"));
        RevTree revTree = revCommit.getTree();
        int count = 0;
        String path = "";
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.reset(revTree);
            while (treeWalk.next()) {
                count++;
                path = treeWalk.getPathString();
            }
        }
        assertEquals(1, count);
        assertEquals(0, path.compareTo(file.getName()));
    }

    @Test
    void commitAmendTestModifiedFile() throws GitAPIException, IOException {
        addFile();
        Commit commit1 = new Commit();
        commit1.setCommitMessage("Test message");

        //command should be executed successfully
        assertTrue(commit1.execute());

        //modify file and call git add
        FileOutputStream out = new FileOutputStream(file);
        out.write(("Test").getBytes(StandardCharsets.UTF_8));
        out.close();
        git.add()
                .addFilepattern(repo.toPath().relativize(file.toPath()).toString())
                .call();
        assertFalse(git.status().call().isClean());

        //initialize a second commit instance and set amend to true
        Commit commit2 = new Commit();
        commit2.setCommitMessage("Test message");
        commit2.setAmend(true);
        assertTrue(commit2.execute());

        //total number of commits should be 1, file content should have changed
        Iterator<RevCommit> iterator = git.log().call().iterator();
        //get the most recent commit.
        RevCommit revCommit = iterator.next();
        RevTree revTree = revCommit.getTree();

        //count the total number of commits including the one already seen
        int numCommits = 1;
        while (iterator.hasNext()) {
            iterator.next();
            numCommits++;
        }
        //total number of commits should be five (4 from initialization + 1 new), not 6
        assertEquals(5, numCommits);

        String path = "";
        //count the files in that commit
        int numFilesInCommit = 0;
        //walk through all files in that commit. Should only be one file.
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.reset(revTree);
            while (treeWalk.next()) {
                numFilesInCommit++;
                //memorize path
                path = treeWalk.getPathString();
            }
        }
        assertEquals(1, numFilesInCommit);
        //path should equal file.getName()
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

    @Test
    void commitAmendTestTwoFiles() throws GitAPIException, IOException {
        addFile();
        Commit commit1 = new Commit();
        commit1.setCommitMessage("Test message");

        //command should be executed successfully
        assertTrue(commit1.execute());

        //add new file
        File file2 = new File(repo, "file2");
        new FileOutputStream(file2).close();
        git.add()
                .addFilepattern(repo.toPath().relativize(file2.toPath()).toString())
                .call();
        assertFalse(git.status().call().isClean());


        //initialize a second commit instance and set amend to true
        Commit commit2 = new Commit();
        commit2.setCommitMessage("Test message");
        commit2.setAmend(true);
        assertTrue(commit2.execute());

        //look at the first commit, it should contain both files
        Iterator<RevCommit> iterator = git.log().call().iterator();
        RevCommit revCommit = iterator.next();
        RevTree revTree = revCommit.getTree();
        List<String> committedFilesPathList = new LinkedList<>();
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.reset(revTree);
            while (treeWalk.next()) {
                committedFilesPathList.add(treeWalk.getPathString());
            }
        }
        //this commit should contain 2 files that should match file and file2
        assertEquals(2, committedFilesPathList.size());
        assertEquals(0, committedFilesPathList.get(0).compareTo(file.getName()));
        assertEquals(0, committedFilesPathList.get(1).compareTo(file2.getName()));

        //count the total number of commits including the one already seen
        int numCommits = 1;
        while (iterator.hasNext()) {
            iterator.next();
            numCommits++;
        }

        //five commits in total
        assertEquals(5, numCommits);


    }


}
