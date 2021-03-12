package dialogviews;

import commands.CloneTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class CloneDialogViewTest extends CloneTest {
  private CloneDialogView cDV;
  private JButton cancelButton;
  private JButton cloneButton;
  private JButton chooseButton;
  private JTextField remoteField;
  private JCheckBox recursiveCheckBox;

  @BeforeEach
  void findComponents() {
    cDV = new CloneDialogView();
    JPanel panel = cDV.getPanel();
    FindComponents find = new FindComponents();
    cancelButton = (JButton) find.getChildByName(panel, "cancelButton");
    assertNotNull(cancelButton);
    chooseButton = (JButton) find.getChildByName(panel, "chooseButton");
    assertNotNull(chooseButton);
    cloneButton = (JButton) find.getChildByName(panel, "cloneButton");
    assertNotNull(cloneButton);
    remoteField = (JTextField) find.getChildByName(panel, "remoteField");
    assertNotNull(remoteField);
    recursiveCheckBox = (JCheckBox) find.getChildByName(panel, "recursiveCheckBox");
    assertNotNull(recursiveCheckBox);
  }

  @Test
  void testCloneButton() {
    MockedConstruction<JFileChooser> jFileChooserMockedConstruction = mockConstruction(JFileChooser.class, (mock, context) -> {
      when(mock.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
      when(mock.getSelectedFile()).thenReturn(remoteDir);
    });
    remoteField.setText(repo.getAbsolutePath());
    recursiveCheckBox.setSelected(true);
    chooseButton.getActionListeners()[0].actionPerformed(new ActionEvent(chooseButton, ActionEvent.ACTION_PERFORMED, null));
    cloneButton.getActionListeners()[0].actionPerformed(new ActionEvent(cloneButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.closeDialogViewCalled);
    assertTrue(guiControllerTestable.setCommandLineCalled);
    jFileChooserMockedConstruction.close();
  }

  @Test
  void testCloneSecondConstructor() {
    remoteField.setText(repo.getAbsolutePath());
    cDV = new CloneDialogView(repo.getAbsolutePath(), remoteDir, true);
    JPanel panel = cDV.getPanel();
    FindComponents find = new FindComponents();
    cloneButton = (JButton) find.getChildByName(panel, "cloneButton");
    assertNotNull(cloneButton);
    cloneButton.getActionListeners()[0].actionPerformed(new ActionEvent(cloneButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.closeDialogViewCalled);
    assertTrue(guiControllerTestable.setCommandLineCalled);
  }

  @Test
  void testCancelButton() {
    cancelButton.getActionListeners()[0].actionPerformed(new ActionEvent(cancelButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.closeDialogViewCalled);
  }

  @Test
  void testMetaData() {
    cDV.update();
    assertNotNull(cDV.getDimension());
    assertNotNull(cDV.getPanel());
    assertNotNull(cDV.getTitle());

  }
}
