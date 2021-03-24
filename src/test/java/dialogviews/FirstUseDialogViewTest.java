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

import commands.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import settings.*;

import javax.swing.*;
import java.awt.event.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FirstUseDialogViewTest extends AbstractCommandTest {
  private FirstUseDialogView firstUseDialogView;
  private JPanel panel;
  private JButton chooseButton;
  private JTextField nameField;
  private JTextField emailField;
  private JButton finishButton;
  private MockedConstruction<JFileChooser> mockedJFileChooser;

  @BeforeEach
  void prepare() {
    firstUseDialogView = new FirstUseDialogView();
    panel = firstUseDialogView.getPanel();
    chooseButton = (JButton) FindComponents.getChildByName(panel, "chooseButton");
    assertNotNull(chooseButton);
    nameField = (JTextField) FindComponents.getChildByName(panel, "nameField");
    assertNotNull(nameField);
    emailField = (JTextField) FindComponents.getChildByName(panel, "emailField");
    assertNotNull(emailField);
    finishButton = (JButton) FindComponents.getChildByName(panel, "finishButton");
    assertNotNull(finishButton);
    //prevent fileChooser from opening window
    mockedJFileChooser = mockConstruction(JFileChooser.class, (mock, context) -> {
      when(mock.showOpenDialog(any())).thenReturn(0);
      when(mock.getSelectedFile()).thenReturn(repo);
    });
  }

  @AfterEach
  void tearDown(){
    mockedJFileChooser.close();
  }


  @Test
  void testViewData() {
    assertEquals(0, firstUseDialogView.getTitle().compareTo("Erstbenutzung"));
    assertEquals(500, firstUseDialogView.getDimension().width);
    assertEquals(150, firstUseDialogView.getDimension().height);
    assertNotNull(firstUseDialogView.getPanel());

  }

  @Test
  void testFinishButton() {
    for (ActionListener listener : chooseButton.getActionListeners()) {
      listener.actionPerformed(new ActionEvent(chooseButton, ActionEvent.ACTION_PERFORMED, "Button clicked"));
    }
    nameField.setText("TestName");
    emailField.setText("TestEmail");


    for (ActionListener listener : finishButton.getActionListeners()) {
      listener.actionPerformed(new ActionEvent(finishButton, ActionEvent.ACTION_PERFORMED, "Button clicked"));
    }
    assertEquals(0, Settings.getInstance().getActiveRepositoryPath().getName().compareTo(repo.getName()));

    assertTrue(guiControllerTestable.closeDialogViewCalled);
  }


  @Test
  void testEmptyName() {
    emailField.setText("TestMail");
    //Set a path
    for (ActionListener listener : chooseButton.getActionListeners()) {
      listener.actionPerformed(new ActionEvent(chooseButton, ActionEvent.ACTION_PERFORMED, "Button clicked"));
    }
    for (ActionListener listener : finishButton.getActionListeners()) {
      listener.actionPerformed(new ActionEvent(finishButton, ActionEvent.ACTION_PERFORMED, "Button clicked"));
    }
    assertEquals(0, Settings.getInstance().getActiveRepositoryPath().getName().compareTo(repo.getName()));
    assertFalse(guiControllerTestable.closeDialogViewCalled);
    assertTrue(guiControllerTestable.errorHandlerECalled);
  }

  @Test
  void testEmptyMail() {
    nameField.setText("TestMail");
    //Set a path
    for (ActionListener listener : chooseButton.getActionListeners()) {
      listener.actionPerformed(new ActionEvent(chooseButton, ActionEvent.ACTION_PERFORMED, "Button clicked"));
    }
    for (ActionListener listener : finishButton.getActionListeners()) {
      listener.actionPerformed(new ActionEvent(finishButton, ActionEvent.ACTION_PERFORMED, "Button clicked"));
    }
    assertEquals(0, Settings.getInstance().getActiveRepositoryPath().getName().compareTo(repo.getName()));
    assertFalse(guiControllerTestable.closeDialogViewCalled);
    assertTrue(guiControllerTestable.errorHandlerECalled);
  }
}
