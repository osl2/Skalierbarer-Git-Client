package git;

import commands.Commit;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommitTest extends AbstractGitTest {

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
        assertTrue(commit.getName() == null);
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
        //create new file
        File file = new File(repo, "file");
        new FileOutputStream(file).close();
        GitFile gitFile = new GitFile(file.getTotalSpace(), file);

        //add file
        git.add()
                .addFilepattern(repo.toPath().relativize(file.toPath()).toString())
                .call();
        Status status = git.status().call();
        Set<String> addedFiles = status.getAdded();
        assertTrue(addedFiles.contains(file.getName()));

        Commit commit = new Commit();
        commit.setCommitMessage("Test");

        //command should be executed successfully
        assertTrue(commit.execute());

        //file should no longer be added and status should be clean
        status = git.status().call();
        addedFiles = status.getAdded();
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


}
