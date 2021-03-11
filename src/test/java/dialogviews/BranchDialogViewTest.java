package dialogviews;

import commands.AbstractCommandTest;
import git.GitBranch;
import git.GitData;
import git.exception.GitException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BranchDialogViewTest extends AbstractCommandTest {
  BranchDialogView branchD;
  JTextField textField;
  JComboBox branchComboBox;
  JComboBox commitComboBox;
  JButton branchButton;

  @BeforeEach
  void createComponents() {
    branchD = new BranchDialogView();
    JPanel frame = branchD.getPanel();
    textField = (JTextField) FindComponents.getChildByName(frame, "nameField");
    assertNotNull(textField);
    branchComboBox = (JComboBox) FindComponents.getChildByName(frame, "branchComboBox");
    assertNotNull(branchComboBox);
    commitComboBox = (JComboBox) FindComponents.getChildByName(frame, "commitComboBox");
    assertNotNull(commitComboBox);
    branchButton = (JButton) FindComponents.getChildByName(frame, "branchButton");
    assertNotNull(branchButton);
  }

  @Test
  void testBranchDialogView() throws GitException {
    textField.setText("NeuerBranch");
    branchComboBox.setSelectedIndex(0);
    commitComboBox.setSelectedIndex(0);
    branchButton.getActionListeners()[0].actionPerformed(new ActionEvent(branchButton, ActionEvent.ACTION_PERFORMED, null));
    GitData data = new GitData();
    List<GitBranch> b = data.getBranches();
    assertEquals(2, b.size());
  }


  @Test
  void testBranchDialogViewNoMessage() {
    branchComboBox.setSelectedIndex(0);
    commitComboBox.setSelectedIndex(0);
    branchButton.getActionListeners()[0].actionPerformed(new ActionEvent(branchButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
  }

  @Test
  void testMetaData() {
    branchD.update();
    assertNotNull(branchD.getDimension());
    assertNotNull(branchD.getTitle());
    assertNotNull(branchD.getPanel());
  }
}
