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
package commands;

import controller.GUIController;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InitTest {
  @TempDir
  protected static File directory;
  static GUIControllerTestable guiControllerTestable;
  static MockedStatic<GUIController> mockedController;
  Init init;

  @BeforeEach
  public void prepareTest() throws IOException {
    guiControllerTestable = new GUIControllerTestable();
    mockedController = mockStatic(GUIController.class);
    mockedController.when(GUIController::getInstance).thenReturn(guiControllerTestable);
    guiControllerTestable.resetTestStatus();
    init = new Init();
    FileUtils.forceMkdir(directory);
    assertTrue(directory.isDirectory());
  }

  @AfterEach
  public void tearDown() throws IOException {
    init = null;
    FileUtils.forceDelete(directory);
    mockedController.close();
  }

  @Test
  void testInitExecute() {
    init.setPathToRepository(directory);
    assertTrue(init.execute());
    File file = new File(directory, ".git");
    assertTrue(file.exists());
    assertEquals("git init", init.getCommandLine());
  }

  @Test
  void testExecuteFail() {
    assertFalse(init.execute());
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
  }

  @Test
  void testGetter() {
    assertNull(init.getCommandLine());
    assertNotNull(init.getDescription());
    assertNotNull(init.getName());
  }

  @Test
  void globalTestCaseInit() {
    MockedConstruction<JFileChooser> jFileChooserMockedConstruction = mockConstruction(JFileChooser.class, (mock, context) -> {
      when(mock.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
      when(mock.getSelectedFile()).thenReturn(directory);
    });
    Init init = new Init();
    init.onButtonClicked();
    File file = directory.listFiles()[0];
    assertEquals(".git", file.getName());
    jFileChooserMockedConstruction.close();
  }
}
