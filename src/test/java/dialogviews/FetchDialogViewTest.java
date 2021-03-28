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

import commands.AbstractRemoteTest;
import commands.Fetch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

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
  void globalFetchTest() {
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
    assertTrue(fDV.canBeOpened());
  }

  @Test
  void testNothingSelected() {
    fetchButton.getActionListeners()[0].actionPerformed(new ActionEvent(fetchButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.errorHandlerMSGCalled | guiControllerTestable.errorHandlerECalled);
  }

  @Test
  void testExecuteFail() {
    MockedConstruction<Fetch> fetchMockedConstruction = mockConstruction(Fetch.class, (mock, context) -> {
      when(mock.execute()).thenReturn(false);
    });
    Object rootNode = fetchTree.getModel().getRoot();
    Object masterBranchNode = fetchTree.getModel().getChild(rootNode, 0);
    Object testCommitNode = fetchTree.getModel().getChild(masterBranchNode, 0);
    assertNotNull(testCommitNode);
    TreePath path = new TreePath(testCommitNode);
    fetchTree.setSelectionPath(path);
    fetchButton.getActionListeners()[0].actionPerformed(new ActionEvent(fetchButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.errorHandlerMSGCalled | guiControllerTestable.errorHandlerECalled);
    fetchMockedConstruction.close();
  }

}

