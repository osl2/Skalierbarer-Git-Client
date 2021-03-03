package commands;

import git.AbstractGitTest;
import git.GitBranch;
import git.GitCommit;
import git.exception.GitException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BranchTest extends AbstractGitTest {

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
    for (int i = 0; i < gitBranches.size(); i++) {
      if (gitBranches.get(i).getName().compareTo("master") == 0) {
        countBranches++;
      } else if (gitBranches.get(i).getName().compareTo("New") == 0) {
        countBranches++;
      }
    }
    assertEquals(2, countBranches);
  }
}
