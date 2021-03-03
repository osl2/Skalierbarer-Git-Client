package commands;

import controller.GUIController;
import git.*;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutTest extends AbstractGitTest {
    private static final String TEST_BRANCH = "testBranch";
    static GUIControllerTestable guiControllerTestable;
    Checkout checkout;
    static MockedStatic<GUIController> mockedController;

    @BeforeAll
    static void setup() {
        guiControllerTestable = new GUIControllerTestable();
        mockedController = mockStatic(GUIController.class);
        mockedController.when(GUIController::getInstance).thenReturn(guiControllerTestable);
        guiControllerTestable.resetTestStatus();
    }

    @AfterAll
    static void tearDown() {
        mockedController.close();
    }

    @Override
    public void init() throws IOException, GitAPIException, GitException, URISyntaxException {
        super.init();
        this.checkout = new Checkout();
    }

    @Test
    void noCommitSetFailsTest() {
        // do not set branch or commit
        assertFalse(checkout.execute());
        // Commandline should be null and not "" as we do not want to override the output in the GUI.
        assertNull(checkout.getCommandLine());
    }

    @Test
    void commitCheckoutTest() throws IOException, GitException {
        Iterator<GitCommit> commitIterator = new GitData().getCommits();
        commitIterator.next();
        GitCommit commit = commitIterator.next();
        checkout.setDestination(commit);
        assertTrue(checkout.execute());

        // Check Commandline output
        assertTrue(checkout.getCommandLine().startsWith("git"));
        // This is not a Branch
        assertFalse(checkout.getCommandLine().contains("-b"));
        assertTrue(checkout.getCommandLine().contains(commit.getHashAbbrev()));
    }

    @Test
    void branchCheckoutTest() throws IOException, GitException, GitAPIException {
        git.branchCreate().setName(TEST_BRANCH).call();
        Iterator<GitBranch> branchIterator = gitData.getBranches().iterator();

        GitBranch branch = branchIterator.next();
        if (branch.equals(gitData.getSelectedBranch())) branch = branchIterator.next();

        checkout.setDestination(branch);

        assertTrue(checkout.execute());

        // Check Commandline output
        assertTrue(checkout.getCommandLine().startsWith("git"));
        assertTrue(checkout.getCommandLine().contains("-b " + TEST_BRANCH));
    }

    @Test
    void metaDataTest() {
        assertNotNull(checkout.getName());
        assertNotNull(checkout.getDescription());
        checkout.onButtonClicked();
        assertTrue(guiControllerTestable.openDialogCalled);
    }

    @Test
    void errorsAreShownToUserTest() throws GitAPIException, GitException, IOException {
        MockedConstruction<GitFacade> gitFacadeMockedConstruction = mockConstruction(GitFacade.class, (mock, context) -> {
            when(mock.checkout(any(GitBranch.class))).thenThrow(new GitException());
            when(mock.checkout(any(GitCommit.class))).thenThrow(new GitException());
        });
        git.branchCreate().setName(TEST_BRANCH).call();
        Iterator<GitBranch> branchIterator = gitData.getBranches().iterator();

        GitBranch branch = branchIterator.next();
        if (branch.equals(gitData.getSelectedBranch())) branch = branchIterator.next();
        checkout.setDestination(branch);
        checkout.execute();
        assertTrue(guiControllerTestable.errorHandlerECalled || guiControllerTestable.errorHandlerMSGCalled);
        guiControllerTestable.resetTestStatus();

        Iterator<GitCommit> commitIterator = new GitData().getCommits();
        commitIterator.next();
        checkout.setDestination(commitIterator.next());
        checkout.execute();
        assertTrue(guiControllerTestable.errorHandlerECalled || guiControllerTestable.errorHandlerMSGCalled);

        gitFacadeMockedConstruction.close();
    }
}
