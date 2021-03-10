package dialogviews;

import commands.AbstractCommandTest;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;

public class RevertDialogViewTest extends AbstractCommandTest {
  RevertDialogView rDV;
  JButton revertButton;
  JTree revertTree;

  @BeforeEach
  void findGuiComponents() {
    rDV = new RevertDialogView();
    JPanel panel = rDV.getPanel();
    revertButton = (JButton) FindComponents.getChildByName(panel, "revertButton");
    assertNotNull(revertButton);
    revertTree = (JTree) FindComponents.getChildByName(panel, "revertTree");
    assertNotNull(revertTree);
  }

  @Test
  void testRevertButton() {
    Object rootNode = revertTree.getModel().getRoot();
    Object masterBranchNode = revertTree.getModel().getChild(rootNode, 0);
    assertNotNull(masterBranchNode);
    Object testCommitNode = revertTree.getModel().getChild(masterBranchNode, 0);
    assertNotNull(testCommitNode);
    TreePath path = new TreePath(testCommitNode);
    revertTree.setSelectionPath(path);
    revertButton.getActionListeners()[0].actionPerformed(new ActionEvent(revertButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.closeDialogViewCalled);
  }

  @Test
  void testMetaData() {
    rDV.update();
    assertNotNull(rDV.getDimension());
    assertNotNull(rDV.getTitle());
  }

  @Test
  void testLoadMoreNode() throws GitAPIException {
    generateCommits(71);
    rDV = new RevertDialogView();
    JPanel panel = rDV.getPanel();
    revertTree = (JTree) FindComponents.getChildByName(panel, "revertTree");
    assertNotNull(revertTree);
    Object rootNode = revertTree.getModel().getRoot();
    Object masterBranchNode = revertTree.getModel().getChild(rootNode, 0);
    assertNotNull(masterBranchNode);
    Object testLoadMoreNode = revertTree.getModel().getChild(masterBranchNode, revertTree.getModel().getChildCount(masterBranchNode) - 1);
    assertNotNull(testLoadMoreNode);
    TreePath path = new TreePath(testLoadMoreNode);
    revertTree.setSelectionPath(path);
    Object lastNode = revertTree.getModel().getChild(masterBranchNode, revertTree.getModel().getChildCount(masterBranchNode) - 1);
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
