package dialogviews;

import commands.AbstractCommandTest;
import git.GitAuthor;
import levels.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import settings.Data;
import settings.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SettingsDialogViewTest extends AbstractCommandTest {
  private JTextField nameField;
  private JTextField eMailField;
  private JComboBox levelComboBox;
  private JCheckBox tooltipsCheckbox;
  private JCheckBox treeViewCheckbox;
  private JButton saveButton;
  private JButton cancelButton;
  private SettingsDialogView sDV;
  private Settings settings = Settings.getInstance();

  @BeforeEach
  void getComponents() {
    FindComponents find = new FindComponents();
    // Set a level in order to create SettingsDialogView.
    List<Level> levels = Data.getInstance().getLevels();
    assertEquals(4, levels.size());
    settings.setLevel(levels.get(0));
    settings.setUseTooltips(false);
    settings.setShowTreeView(false);
    sDV = new SettingsDialogView();
    JPanel panel = sDV.getPanel();
    nameField = (JTextField) find.getChildByName(panel, "nameField");
    assertNotNull(nameField);
    eMailField = (JTextField) find.getChildByName(panel, "eMailField");
    assertNotNull(eMailField);
    levelComboBox = (JComboBox) find.getChildByName(panel, "levelComboBox");
    assertNotNull(levelComboBox);
    tooltipsCheckbox = (JCheckBox) find.getChildByName(panel, "tooltipsCheckbox");
    assertNotNull(tooltipsCheckbox);
    treeViewCheckbox = (JCheckBox) find.getChildByName(panel, "treeViewCheckbox");
    assertNotNull(treeViewCheckbox);
    saveButton = (JButton) find.getChildByName(panel, "saveButton");
    assertNotNull(saveButton);
    cancelButton = (JButton) find.getChildByName(panel, "cancelButton");
    assertNotNull(cancelButton);
  }

  @Test
  void testSettingsDialogViewChangeLevel() {
    levelComboBox.setSelectedIndex(2);
    saveButton.getActionListeners()[0].actionPerformed(new ActionEvent(saveButton, ActionEvent.ACTION_PERFORMED, null));
    assertEquals(Data.getInstance().getLevels().get(2).getName(), settings.getLevel().getName());
  }

  @Test
  void testSettingsDialogViewChangeUser() {
    nameField.setText("New User");
    eMailField.setText("New eMail");
    saveButton.getActionListeners()[0].actionPerformed(new ActionEvent(saveButton, ActionEvent.ACTION_PERFORMED, null));
    GitAuthor author = settings.getUser();
    assertEquals("New User", author.getName());
    assertEquals("New eMail", author.getEmail());
  }

  @Test
  void testSettingsDialogViewChangeCheckbox() {
    assertFalse(tooltipsCheckbox.isSelected());
    assertFalse(treeViewCheckbox.isSelected());
    tooltipsCheckbox.setSelected(true);
    treeViewCheckbox.setSelected(true);
    saveButton.getActionListeners()[0].actionPerformed(new ActionEvent(saveButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(settings.showTreeView());
    assertTrue(settings.useTooltips());
  }

  @Test
  void testSettingsDialogViewCancel() {
    cancelButton.getActionListeners()[0].actionPerformed(new ActionEvent(saveButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.closeDialogViewCalled);
  }

  @Test
  void testGetter() {
    assertNotNull(sDV.getDimension());
    assertEquals("Einstellungen", sDV.getTitle());
  }
}
