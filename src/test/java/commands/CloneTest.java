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

import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CloneTest extends AbstractCommandTest {

  protected static File remoteDir = new File("src" + System.getProperty("file.separator") + "test" +
          System.getProperty("file.separator") + "resources" + System.getProperty("file.separator") + "Test" +
          System.getProperty("file.separator") + "remote");

  @BeforeEach
  protected void createRemoteDir() throws IOException {
    deleteDir(remoteDir);
    FileUtils.forceMkdir(remoteDir);
  }

  @AfterEach
  protected void cleanUp() {
    assertTrue(deleteDir(remoteDir.getAbsoluteFile()));
  }

  @Test
  void testCloneExecute() {
    Clone clone = new Clone();
    clone.cloneRecursive(true);
    clone.setDestination(remoteDir);
    clone.setGitURL(repo.getPath());
    assertTrue(clone.execute());
    File[] files = remoteDir.listFiles();
    assertEquals(2, files.length);
    File gitFile = null;
    for (int i = 0; i < files.length; i++) {
      if (files[i].getName().compareTo(".git") == 0) {
        gitFile = files[i];
      }
    }
    assertNotNull(gitFile);
    assertNotNull(clone.getCommandLine());
  }

  @Test
  void testMetaData() {
    Clone clone = new Clone();
    assertNotNull(clone.getName());
    assertNotNull(clone.getDescription());
    assertNull(clone.getCommandLine());
    clone.onButtonClicked();
    assertTrue(guiControllerTestable.openDialogCalled);
  }

  @Test
  void testExceptionHandling() throws GitException {
    Clone clone = new Clone();
    assertFalse(clone.execute());
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
  }
}
