/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
    // Set a level in order to create SettingsDialogView.
    List<Level> levels = Data.getInstance().getLevels();
    assertEquals(4, levels.size());
    settings.setLevel(levels.get(0));
    settings.setUseTooltips(false);
    settings.setShowTreeView(false);
    sDV = new SettingsDialogView();
    JPanel panel = sDV.getPanel();
    nameField = (JTextField) FindComponents.getChildByName(panel, "nameField");
    assertNotNull(nameField);
    eMailField = (JTextField) FindComponents.getChildByName(panel, "eMailField");
    assertNotNull(eMailField);
    levelComboBox = (JComboBox) FindComponents.getChildByName(panel, "levelComboBox");
    assertNotNull(levelComboBox);
    tooltipsCheckbox = (JCheckBox) FindComponents.getChildByName(panel, "tooltipsCheckbox");
    assertNotNull(tooltipsCheckbox);
    treeViewCheckbox = (JCheckBox) FindComponents.getChildByName(panel, "treeViewCheckbox");
    assertNotNull(treeViewCheckbox);
    saveButton = (JButton) FindComponents.getChildByName(panel, "saveButton");
    assertNotNull(saveButton);
    cancelButton = (JButton) FindComponents.getChildByName(panel, "cancelButton");
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
