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

import git.GitData;
import git.GitRemote;
import git.exception.GitException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class FetchTest extends AbstractRemoteTest {

  @Test
  void testFetchExecuteRemote() {
    Fetch fetch = new Fetch();
    fetch.addRemote(gitData.getRemotes().get(0));
    assertTrue(fetch.execute());
    assertFalse(guiControllerTestable.errorHandlerMSGCalled | guiControllerTestable.errorHandlerECalled);
    assertTrue(fetch.getCommandLine().contains("git fetch"));
  }

  @Test
  void testFetchExecuteRemoteBranch() throws GitException {
    Fetch fetch = new Fetch();
    fetch.addBranch(gitData.getRemotes().get(0), gitData.getBranches(gitData.getRemotes().get(0)).get(0));
    assertTrue(fetch.execute());
    assertFalse(guiControllerTestable.errorHandlerMSGCalled | guiControllerTestable.errorHandlerECalled);
    assertTrue(fetch.getCommandLine().contains("git fetch"));
  }

  @Test
  void testMetaData() {
    Fetch fetch = new Fetch();
    assertNotNull(fetch.getDescription());
    assertNotNull(fetch.getName());
    assertNull(fetch.getCommandLine());
    fetch.onButtonClicked();
    assertTrue(guiControllerTestable.openDialogCalled);
  }

  @Test
  void testDialogViewDoesNotOpen() {
    MockedConstruction<GitData> mockConst = mockConstruction(GitData.class, (mock, context) -> {
      when(mock.getBranches(any(GitRemote.class))).thenReturn(new ArrayList<>());
    });
    Fetch fetch = new Fetch();
    fetch.onButtonClicked();
    assertFalse(guiControllerTestable.openDialogCalled);
    mockConst.close();
  }
}
