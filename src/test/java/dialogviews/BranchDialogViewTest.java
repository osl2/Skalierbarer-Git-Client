package dialogviews;

import controller.GUIController;
import git.AbstractGitTest;
import git.GitBranch;
import git.GitData;
import git.exception.GitException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

public class BranchDialogViewTest extends AbstractGitTest {
  static GUIControllerTestable guiControllerTestable;
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

  private static Component getChildByName(Component parent, String child) {
    if (child.equals(parent.getName())) {
      return parent;
    }
    if (parent instanceof Container) {
      Component[] allChildren = (parent instanceof JMenu) ? ((JMenu) parent).getMenuComponents() : ((Container) parent).getComponents();

      for (int i = 0; i < allChildren.length; i++) {
        Component children = getChildByName(allChildren[i], child);
        if (children != null) {
          return children;
        }
      }
    }
    return null;
  }

  @Test
  public void testBranchDialogView() throws GitException, IOException {
    BranchDialogView branchD = new BranchDialogView();
    JFrame frame = new JFrame(branchD.getTitle());
    frame.setSize(branchD.getDimension());
    frame.add(branchD.getPanel());
    JTextField textField;
    textField = (JTextField) getChildByName(frame, "nameField");
    assertTrue(textField != null);
    textField.setText("NeuerBranch");
    JComboBox branchComboBox = (JComboBox) getChildByName(frame, "branchComboBox");
    assertTrue(branchComboBox != null);
    branchComboBox.setSelectedIndex(0);
    JComboBox commitComboBox = (JComboBox) getChildByName(frame, "commitComboBox");
    assertTrue(commitComboBox != null);
    commitComboBox.setSelectedIndex(0);
    JButton branchButton = (JButton) getChildByName(frame, "branchButton");
    assertTrue(branchButton != null);
    branchButton.getActionListeners()[0].actionPerformed(new ActionEvent(branchButton, ActionEvent.ACTION_PERFORMED, null));
    GitData data = new GitData();
    List<GitBranch> b = data.getBranches();
    assertTrue(b.size() == 2);
  }
}
