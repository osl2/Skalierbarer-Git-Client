package git;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import settings.Settings;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


/**
 * Is testing GitData
 */
public class GitDataTest extends AbstractGitTest {
    private static final String[][] COMMIT_DATA = {
            new String[]{"Tester 1", "tester1@example.com", "Commit 1"},
            new String[]{"Tester 2", "tester2@example.com", "Commit 2"},
            new String[]{"Tester 3", "tester3@example.com", "Commit 3"},
            new String[]{"Tester 4", "tester4@example.com", "Commit 4"},
    };


    @Test
    public void getBranchesTest() throws GitAPIException {
        List<GitBranch> branchesGitData = gitData.getBranches();
        List<Ref> branchesJGit = git.branchList().call();
        assertEquals(branchesGitData.size(), branchesJGit.size());
        // TODO: mehr testen

    }

    @Test
    public void commitsAreSortedFromNewestToOldestTest() {
        int size = 0;
        Iterator<GitCommit> it = gitData.getCommits();
        while (it.hasNext()) {
            size++;
            it.next();
        }
        assertEquals(COMMIT_DATA.length, size);
        it = gitData.getCommits();
        int i = size - 1;
        while (it.hasNext()) {
            assertEquals(new GitAuthor(COMMIT_DATA[i][0], COMMIT_DATA[i][1]), it.next().getAuthor());
            i--;
        }

    }

    @Test
    public void commitsHaveCorrectCommitMessageTest() {
        int i = COMMIT_DATA.length - 1;
        Iterator<GitCommit> it = gitData.getCommits();
        while (it.hasNext()) {
            assertEquals(COMMIT_DATA[i--][2],
                    it.next().getMessage());
        }
    }

    @Test
    public void findsAllBranches() throws GitAPIException {
        git.branchCreate().setName("Test1").call();
        git.branchCreate().setName("Test2").call();
        git.branchCreate().setName("Test3").call();

        List<GitBranch> branches = gitData.getBranches();
        assertEquals(4, branches.size()); // master + 3
    }

    @Test
    public void selectedBranchIsUpdated() throws GitAPIException, IOException {
        git.branchCreate().setName("Test1").call();
        git.branchCreate().setName("Test2").call();
        git.branchCreate().setName("Test3").call();

        List<GitBranch> branches = gitData.getBranches();
        GitBranch selected = branches.get(2); // arbitrary selection
        assertNotEquals(selected, gitData.getSelectedBranch());
        git.checkout().setName(selected.getFullName()).call();
        assertEquals(selected, gitData.getSelectedBranch());
    }
}

