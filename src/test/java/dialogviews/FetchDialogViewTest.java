package dialogviews;

import commands.AbstractRemoteTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;

public class FetchDialogViewTest extends AbstractRemoteTest {
  JTree fetchTree;
  JButton fetchButton;
  FetchDialogView fDV;

  @BeforeEach
  void getGuiComponents() {
    fDV = new FetchDialogView();
    JPanel panel = fDV.getPanel();
    fetchButton = (JButton) FindComponents.getChildByName(panel, "fetchButton");
    assertNotNull(fetchButton);
    fetchTree = (JTree) FindComponents.getChildByName(panel, "fetchTree");
    assertNotNull(fetchTree);
  }

  @Test
  void testFetchButton() {
    Object rootNode = fetchTree.getModel().getRoot();
    Object masterBranchNode = fetchTree.getModel().getChild(rootNode, 0);
    Object testCommitNode = fetchTree.getModel().getChild(masterBranchNode, 0);
    assertNotNull(testCommitNode);
    TreePath path = new TreePath(testCommitNode);
    fetchTree.setSelectionPath(path);
    fetchButton.getActionListeners()[0].actionPerformed(new ActionEvent(fetchButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.setCommandLineCalled);
    assertFalse(guiControllerTestable.errorHandlerMSGCalled | guiControllerTestable.errorHandlerECalled);
    assertTrue(guiControllerTestable.closeDialogViewCalled);
  }

  @Test
  void testMetaData() {
    fDV.update();
    assertNotNull(fDV.getDimension());
    assertNotNull(fDV.getTitle());
    assertNotNull(fDV.getPanel());
    assertTrue(fDV.isOpen());
  }
}
