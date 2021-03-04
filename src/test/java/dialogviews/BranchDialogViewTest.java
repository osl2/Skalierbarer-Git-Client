package dialogviews;

import commands.AbstractCommandTest;
import git.GitBranch;
import git.GitData;
import git.exception.GitException;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static dialogviews.FindComponents.getChildByName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BranchDialogViewTest extends AbstractCommandTest {

  @Test
  void testBranchDialogView() throws GitException {
    BranchDialogView branchD = new BranchDialogView();
    JPanel frame = branchD.getPanel();
    JTextField textField;
    textField = (JTextField) getChildByName(frame, "nameField");
    assertNotNull(textField);
    textField.setText("NeuerBranch");
    JComboBox branchComboBox = (JComboBox) getChildByName(frame, "branchComboBox");
    assertNotNull(branchComboBox);
    branchComboBox.setSelectedIndex(0);
    JComboBox commitComboBox = (JComboBox) getChildByName(frame, "commitComboBox");
    assertNotNull(commitComboBox);
    commitComboBox.setSelectedIndex(0);
    JButton branchButton = (JButton) getChildByName(frame, "branchButton");
    assertNotNull(branchButton);
    branchButton.getActionListeners()[0].actionPerformed(new ActionEvent(branchButton, ActionEvent.ACTION_PERFORMED, null));
    GitData data = new GitData();
    List<GitBranch> b = data.getBranches();
    assertEquals(2, b.size());
  }
}
