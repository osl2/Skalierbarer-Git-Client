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

import git.GitBranch;
import git.GitCommit;
import git.GitData;
import git.GitFacade;
import git.exception.GitException;
import levels.Level;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import settings.Data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class PullTest extends AbstractRemoteTest {

  @BeforeEach
  void configLevels() {
    Level level = Data.getInstance().getLevels().get(0);
    settings.setLevel(level);
  }

  @Test
  void testMetaData() {
    Pull pull = new Pull();
    assertFalse(pull.execute());
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    assertNotNull(pull.getName());
    assertNotNull(pull.getDescription());
    assertNull(pull.getCommandLine());
    pull.onButtonClicked();
    assertTrue(guiControllerTestable.openDialogCalled);
  }

  @Test
  void testNoBranches() throws GitException {
    Pull pull = new Pull();
    GitData data = new GitData();
    pull.setRemoteBranch(data.getBranches(data.getRemotes().get(0)).get(0));
    pull.setRemote(data.getRemotes().get(0));
    MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) -> {
      when(mock.getBranches()).thenThrow(new GitException(""));
    });
    assertFalse(pull.execute());
    assertTrue(guiControllerTestable.errorHandlerECalled);
    gitDataMockedConstruction.close();
  }

  @Test
  void testOnButtonClickedFail() {
    MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) -> {
      when(mock.getRemotes()).thenReturn(new ArrayList<>());
    });
    Pull pull = new Pull();
    pull.onButtonClicked();
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    gitDataMockedConstruction.close();
  }

  @Test
  void testExecuteWithLocallyNotExistingBranch() throws IOException, GitAPIException, GitException {
    // Create a new Commit on a new Branch to pull.
    git.checkout().setCreateBranch(true).setName("Neu").call();
    git = Git.open(repo);
    FileWriter fr = new FileWriter(textFile, true);
    fr.write("pull");
    fr.close();
    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 5", "tester5@example.com").setSign(false)
            .setMessage("Commit 5").call();
    // Fetch the new Branch to update the remote Branches.
    GitFacade facade = new GitFacade();
    facade.fetchRemotes(gitData.getRemotes());
    List<GitBranch> branchList = gitData.getBranches(gitData.getRemotes().get(0));
    assertEquals(2, branchList.size());
    GitBranch newBranch = null;
    for (int i = 0; i < branchList.size(); i++) {
      if (branchList.get(i).getName().compareTo("Neu") == 0) {
        newBranch = branchList.get(i);
      }
    }
    assertNotNull(newBranch);
    assertEquals(1, gitData.getBranches().size());
    Pull pull = new Pull();
    pull.setRemote(gitData.getRemotes().get(0));
    pull.setRemoteBranch(newBranch);
    assertTrue(pull.execute());
    assertNotNull(pull.getCommandLine());
  }

  @Test
  void failOnException() throws GitException, GitAPIException, IOException {
    git.checkout().setCreateBranch(true).setName("Neu").call();
    git = Git.open(repo);
    FileWriter fr = new FileWriter(textFile, true);
    fr.write("pull");
    fr.close();
    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 5", "tester5@example.com").setSign(false)
            .setMessage("Commit 5").call();
    // Fetch the new Branch to update the remote Branches.
    GitFacade facade = new GitFacade();
    facade.fetchRemotes(gitData.getRemotes());
    List<GitBranch> branchList = gitData.getBranches(gitData.getRemotes().get(0));
    assertEquals(2, branchList.size());
    GitBranch newBranch = null;
    for (int i = 0; i < branchList.size(); i++) {
      if (branchList.get(i).getName().compareTo("Neu") == 0) {
        newBranch = branchList.get(i);
      }
    }
    assertNotNull(newBranch);
    assertEquals(1, gitData.getBranches().size());
    Pull pull = new Pull();
    pull.setRemote(gitData.getRemotes().get(0));
    pull.setRemoteBranch(newBranch);
    MockedConstruction<GitFacade> gitFacadeMockedConstruction = mockConstruction(GitFacade.class, (mock, context) -> {
      when(mock.branchOperation(any(GitCommit.class), anyString())).thenThrow(new GitException(""));
    });
    assertTrue(pull.execute());
    gitFacadeMockedConstruction.close();
  }
}
