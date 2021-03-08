package git;

import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GitBranchTest extends AbstractGitTest {

  @Test
  public void branchRefIsUpdated() throws GitAPIException, GitException {
    git.branchCreate().setName("Test1").call();
    git.branchCreate().setName("Test2").call();
    git.branchCreate().setName("Test3").call();

    List<GitBranch> branches = gitData.getBranches();
    GitBranch selected = branches.get(2); // arbitrary selection
    GitCommit oldHead = selected.getCommit();
    oldHead.getMessage();
    git.checkout().setName(selected.getFullName()).call();
    git.commit().setCommitter("Test2", "e@example.com").setSign(false).setMessage("msg").call();
    assertNotEquals(oldHead.getHash(), selected.getCommit().getHash());
  }

  @Test
  public void branchNameIsCorrectSpecialCharacters() throws GitAPIException, GitException {
    String[] expectedBranches = new String[]{"Test2", "Test3", "wip/test1"};
    for (String b : expectedBranches) {
      git.branchCreate().setName(b).call();
    }

    String[] branchNames = gitData.getBranches().stream()
        .map(GitBranch::getName)
        .filter(b -> !b.equals("master")) // dont include master
        .toArray(String[]::new);

    Arrays.sort(expectedBranches);
    Arrays.sort(branchNames);

    assertArrayEquals(expectedBranches, branchNames);

  }

  @Test
  public void getNameTest(){
    String name1 = "name1ToTest";
    String name2 = "refs/heads/nameToTest";
    String name3 = "refs/remotes/nameToTest";
    String name4 = "refs/heads/name/master/derBranch";
    GitBranch branch1 = new GitBranch(name1);
    GitBranch branch2 = new GitBranch(name2);
    GitBranch branch3 = new GitBranch(name3);
    GitBranch branch4 = new GitBranch(name4);
    assertEquals(name1, branch1.getName());
    assertEquals("nameToTest", branch2.getName());
    assertEquals(name2, branch2.getFullName());
    assertEquals("nameToTest", branch3.getName());
    assertEquals(name3, branch3.getFullName());
    assertEquals("name/master/derBranch", branch4.getName());


  }
}
