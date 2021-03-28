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

import git.GitCommit;
import git.GitData;
import git.GitFileConflict;
import git.exception.GitException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevertTest extends AbstractCommandTest {

  @Test
  void testExecute() throws IOException, GitException {
    GitCommit commit = gitData.getCommits().next();
    Revert revert = new Revert();
    revert.setChosenCommit(commit);
    assertEquals(commit.getMessage(), revert.getChosenCommit().getMessage());
    assertTrue(revert.execute());
    assertNotNull(revert.getCommandLine());
  }

  @Test
  void testMetaData() {
    Revert revert = new Revert();
    assertNull(revert.getCommandLine());
    assertNotNull(revert.getName());
    assertNotNull(revert.getDescription());
    revert.onButtonClicked();
    assertTrue(guiControllerTestable.openDialogCalled);
  }

  @Test
  void openFails() {
    MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) -> {
      when(mock.getBranches()).thenReturn(new ArrayList<>());
    });
    Revert revert = new Revert();
    revert.onButtonClicked();
    assertFalse(guiControllerTestable.openDialogCalled);
    gitDataMockedConstruction.close();
  }

  @Test
  void openFailsBecauseException() {
    MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) -> {
      when(mock.getBranches()).thenThrow(new GitException(""));
    });
    Revert revert = new Revert();
    revert.onButtonClicked();
    assertTrue(guiControllerTestable.errorHandlerECalled);
    gitDataMockedConstruction.close();
  }

  @Test
  void testExceptionHandling() throws IOException {
    MockedConstruction<GitCommit> gitCommitMockedConstruction = mockConstruction(GitCommit.class, (mock, context) -> {
      when(mock.revert()).thenThrow(new GitException(""));
    });
    GitData data = new GitData();
    GitCommit commit = data.getSelectedBranch().getCommit();
    Revert revert = new Revert();
    revert.setChosenCommit(commit);
    assertFalse(revert.execute());
    assertTrue(guiControllerTestable.errorHandlerECalled);
    gitCommitMockedConstruction.close();
  }

  @Test
  void testFalseInput() {
    Revert revert = new Revert();
    assertFalse(revert.execute());
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
  }

  @Test
  void testRevertConflicts() throws IOException, GitException {
    GitFileConflict conflict = mock(GitFileConflict.class);
    when(conflict.apply()).thenReturn(false).thenReturn(true);
    GitCommit mockCommit = mock(GitCommit.class);
    when(mockCommit.revert()).thenReturn(Collections.singletonList(conflict));
    Revert revert = new Revert();
    revert.setChosenCommit(mockCommit);
    assertFalse(revert.execute());
    assertTrue(revert.execute());
  }
}
