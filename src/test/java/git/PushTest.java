package git;

import commands.AbstractRemoteTest;
import commands.Push;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.URIish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PushTest extends AbstractRemoteTest {
    private Push push;
    private final String REMOTE_NAME = "testRemote";
    private final String BRANCH_NAME = "testBranch";
    private GitRemote remote;
    private GitBranch localBranch;

    @BeforeEach
    void setupPush() throws URISyntaxException, GitAPIException {
        push = new Push();
        remote = new GitRemote(REMOTE_URI, "user", REMOTE_NAME);
        URIish remote_uri = new URIish(REMOTE_URI);
        //update git
        git = GitData.getJGit();
        repository = GitData.getRepository();
        git.remoteAdd().setUri(remote_uri).setName(REMOTE_NAME).call();

        boolean branchExists = false;
        List<Ref> branches = git.branchList().call();
        for (Ref ref : branches) {
            if (new GitBranch(ref.getName()).getName().compareTo(BRANCH_NAME) == 0) {
                branchExists = true;
            }
        }

        if (!branchExists) {
            git.checkout().setCreateBranch(true).setName(BRANCH_NAME).call();
        }

        //reload list of branches
        branches = git.branchList().call();
        for (Ref branchRef : branches) {
            GitBranch branch = new GitBranch(branchRef.getName());
            if (branch.getName().compareTo(BRANCH_NAME) == 0) {
                localBranch = branch;
            }
        }
        assertNotNull(localBranch);
    }

    @Test
    void getNameTest() {
        assertEquals(0, push.getName().compareTo("Push"));
    }

    @Test
    void getCommandLineTest() {
        push.setRemote(remote);
        push.setLocalBranch(localBranch);
        assertEquals(0, push.getCommandLine().compareTo("git push testRemote testBranch"));

        push.setRemoteBranch("remoteBranch");
        assertEquals(0, push.getCommandLine().compareTo("git push testRemote testBranch:remoteBranch"));

    }

    @Test
    void getDescriptionTest() {
        String description = "Lädt die lokalen Einbuchungen aus dem aktuellen Branch in das Online-Verzeichnis hoch";
        assertEquals(0, push.getDescription().compareTo(description));
    }

    @Test
    void testExecuteNoLocalBranchSet() {
        push.setRemote(remote);
        assertFalse(push.execute());
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    @Test
    void testExecuteNoRemoteSet() {
        push.setLocalBranch(localBranch);
        assertFalse(push.execute());
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    @Test
    void onButtonClickedTest() {
        push.onButtonClicked();
        assertTrue(guiControllerTestable.openDialogCalled);
    }

    @Test
    void testExecuteNoRemoteBranchSet() throws GitAPIException {
        push.setLocalBranch(localBranch);
        push.setRemote(remote);
        assertTrue(push.execute());

        git.fetch().setRemote(REMOTE_NAME).call();
        List<Ref> remoteBranches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        assertFalse(remoteBranches.isEmpty());
        Ref correct = null;
        for (Ref remoteBranch : remoteBranches) {
            GitBranch remoteGitBranch = new GitBranch(remoteBranch);
            if (remoteGitBranch.getName().compareTo(localBranch.getName()) == 0) {
                correct = remoteBranch;
            }
        }
        assertNotNull(correct);

    }


}
