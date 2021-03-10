package dialogviews;

import commands.AbstractCommandTest;
import commands.Checkout;
import git.GitBranch;
import git.GitData;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutDialogViewTest extends AbstractCommandTest {

    private JTree tree1;
    private JButton abortButton;
    private JButton okButton;
    private CheckoutDialogView checkoutDialogView;

    private RevCommit targetRevCommit;

    @Override
    public void init() throws IOException, GitAPIException, GitException, URISyntaxException {
        super.init();
        Iterator<RevCommit> revCommitIterator = git.log().call().iterator();
        revCommitIterator.next();
        revCommitIterator.next();
        RevCommit branchPoint = revCommitIterator.next();
        targetRevCommit = revCommitIterator.next();
        git.checkout()
                .setCreateBranch(true)
                .setName("testBranch")
                .setStartPoint(branchPoint)
                .call();
        git.commit()
                .setMessage("TestBranch-Commit")
                .setAuthor("Branch-Author", "branch@example.com")
                .setSign(false)
                .call();
        git.checkout().setName("master").call();
    }

    @BeforeEach
    void getComponents() {
        checkoutDialogView = new CheckoutDialogView();
        JPanel panel = checkoutDialogView.getPanel();
        tree1 = (JTree) FindComponents.getChildByName(panel, "tree1");
        okButton = (JButton) FindComponents.getChildByName(panel, "okButton");
        abortButton = (JButton) FindComponents.getChildByName(panel, "abortButton");

        assertNotNull(tree1);
        assertNotNull(okButton);
        assertNotNull(abortButton);
    }

    private void checkoutCommit() throws IOException, GitAPIException {
        Object rootNode = tree1.getModel().getRoot();
        Object masterBranchNode = tree1.getModel().getChild(rootNode, 0);
        Object testCommitNode = tree1.getModel().getChild(masterBranchNode, 3);
        assertNotNull(testCommitNode);
        TreePath path = new TreePath(testCommitNode);
        tree1.setSelectionPath(path);
        ActionEvent okClicked = new ActionEvent(okButton, ActionEvent.ACTION_PERFORMED, null);
        assertNotEquals(targetRevCommit, git.log().call().iterator().next());
        assertNotEquals("testBranch", gitData.getSelectedBranch().getName());
        Arrays.stream(okButton.getActionListeners()).forEach(l -> {
                    l.actionPerformed(okClicked);
                }
        );
    }

    private void checkoutBranch() throws IOException {
        Object rootNode = tree1.getModel().getRoot();
        Object testBranchNode = tree1.getModel().getChild(rootNode, 1);
        assertNotNull(testBranchNode);
        TreePath path = new TreePath(testBranchNode);
        tree1.setSelectionPath(path);
        ActionEvent okClicked = new ActionEvent(okButton, ActionEvent.ACTION_PERFORMED, null);
        assertNotEquals("testBranch", gitData.getSelectedBranch().getName());
        Arrays.stream(okButton.getActionListeners()).forEach(l -> {
                    l.actionPerformed(okClicked);
                }
        );
    }


    @Test
    void checkoutBranchTest() throws IOException {
        checkoutBranch();
        assertEquals("testBranch", gitData.getSelectedBranch().getName());
    }

    @Test
    void checkoutCommitTest() throws IOException, GitAPIException {
        checkoutCommit();
        assertEquals(targetRevCommit, git.log().call().iterator().next());
    }


    @Test
    void failsOnNoSelection() {
        ActionEvent okClicked = new ActionEvent(okButton, ActionEvent.ACTION_PERFORMED, null);
        Arrays.stream(okButton.getActionListeners()).forEach(l -> {
                    l.actionPerformed(okClicked);
                }
        );
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    @Test
    void notifyUserOnBranchCheckoutFail() throws IOException {
        MockedConstruction<Checkout> checkoutMockedConstruction = mockConstruction(Checkout.class, (mock, context) -> {
            when(mock.execute()).thenReturn(false);
        });
        checkoutBranch();
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
        checkoutMockedConstruction.close();
    }

    @Test
    void notifyUserOnCommitCheckoutFail() throws IOException, GitAPIException {
        MockedConstruction<Checkout> checkoutMockedConstruction = mockConstruction(Checkout.class, (mock, context) -> {
            when(mock.execute()).thenReturn(false);
        });
        checkoutCommit();
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
        checkoutMockedConstruction.close();
    }

    @Test
    void failOnGitExceptionTreeGenerator() {
        MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) -> {
            when(mock.getBranches()).thenThrow(new GitException());
            when(mock.getCommits()).thenThrow(new GitException());
        });
        checkoutDialogView = new CheckoutDialogView();
        assertTrue(guiControllerTestable.errorHandlerECalled);

        gitDataMockedConstruction.close();
    }

    @Test
    void failOnGitExceptionBranchBuilder() throws GitException, IOException {
        GitBranch mockedBranch = mock(GitBranch.class);
        when(mockedBranch.getCommits()).thenThrow(new GitException());

        MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) -> {
            when(mock.getBranches()).thenReturn(Collections.singletonList(mockedBranch));
        });
        checkoutDialogView = new CheckoutDialogView();
        assertTrue(guiControllerTestable.errorHandlerECalled);

        gitDataMockedConstruction.close();
    }

    @Test
    void loadMoreNodeTest() throws GitAPIException {
        git.branchDelete().setBranchNames("testBranch").setForce(true).call();
        generateCommits(71);
        //checkoutDialogView = new CheckoutDialogView();
        checkoutDialogView.update();
        // Select loadMoreNode
        Object rootNode = tree1.getModel().getRoot();
        Object masterBranchNode = tree1.getModel().getChild(rootNode, 0);
        Object testLoadMoreNode = tree1.getModel().getChild(masterBranchNode, tree1.getModel().getChildCount(masterBranchNode) - 1);
        assertNotNull(testLoadMoreNode);
        TreePath path = new TreePath(testLoadMoreNode);
        tree1.setSelectionPath(path);
        Object lastNode = tree1.getModel().getChild(masterBranchNode, tree1.getModel().getChildCount(masterBranchNode) - 1);
        assertNotEquals(lastNode, testLoadMoreNode);

    }

    private void generateCommits(int amount) throws GitAPIException {
        for (int i = 0; i < amount; i++) {
            git.commit()
                    .setCommitter("Author " + i, i + "@example.com")
                    .setMessage("New Commit " + i)
                    .setSign(false)
                    .call();
        }
    }

}
