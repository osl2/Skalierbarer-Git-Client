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
