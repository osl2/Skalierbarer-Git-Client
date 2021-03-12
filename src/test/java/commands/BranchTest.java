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
import git.exception.GitException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest extends AbstractCommandTest {

  @Test
  void testExecute() throws GitException {
    GitBranch branch = gitData.getBranches().get(0);
    GitCommit commit = branch.getCommit();
    Branch branchCommand = new Branch();
    branchCommand.setBranchPoint(commit);
    String name = "New";
    branchCommand.setBranchName(name);
    assertTrue(branchCommand.execute());
    List<GitBranch> gitBranches = gitData.getBranches();
    int countBranches = 0;
    for (GitBranch gitBranch : gitBranches) {
      if (gitBranch.getName().compareTo("master") == 0) {
        countBranches++;
      } else if (gitBranch.getName().compareTo("New") == 0) {
        countBranches++;
      }
    }
    // Check if both branches exist.
    assertEquals(2, countBranches);
    String commandLine = branchCommand.getCommandLine();
    assertTrue(commandLine.startsWith("git branch"));
    // Check if the commandLine contains the name of the new Branch.
    assertTrue(commandLine.contains(name));
  }

  @Test
  void failTestWithNoCommitSet() {
    Branch branch = new Branch();
    assertFalse(branch.execute());
    // Test if commandLine is null if execution fails.
    assertNull(branch.getCommandLine());
  }

  @Test
  void testExistingGetters() {
    Branch branch = new Branch();
    assertNotNull(branch.getName());
    assertNotNull(branch.getDescription());
    assertNull(branch.getCommandLine());
    branch.onButtonClicked();
    assertTrue(guiControllerTestable.openDialogCalled);
  }

  @Test
  void testNotValidBranchName() throws IOException {
    Branch branch = new Branch();
    GitBranch master = gitData.getSelectedBranch();
    GitCommit commit = master.getCommit();
    String notValidName = "N   ha   ?";
    branch.setBranchName(notValidName);
    branch.setBranchPoint(commit);
    assertFalse(branch.execute());
    // Check if Exception was shown to the user.
    assertTrue(guiControllerTestable.errorHandlerECalled);
  }
}
