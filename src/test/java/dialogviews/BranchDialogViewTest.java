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
