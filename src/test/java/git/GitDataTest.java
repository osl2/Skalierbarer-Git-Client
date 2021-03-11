package git;


import git.exception.*;
import org.apache.commons.io.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import settings.*;

import java.io.*;
import java.util.*;
import git.exception.*;
import org.apache.commons.io.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import settings.*;

import java.io.*;
import java.util.*;
import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


/**
 * Is testing GitData
 */
class GitDataTest extends AbstractGitTest {
  private static final String[][] COMMIT_DATA = {
      new String[]{"Tester 1", "tester1@example.com", "Commit 1"},
      new String[]{"Tester 2", "tester2@example.com", "Commit 2"},
      new String[]{"Tester 3", "tester3@example.com", "Commit 3"},
      new String[]{"Tester 4", "tester4@example.com", "Commit 4"},
  };
  @TempDir
  protected static File newTempFile;

  @Test
  void getBranchesTest() throws GitAPIException, GitException {
    git.branchCreate().setName("TestBranch").call();
    List<GitBranch> branchesGitData = gitData.getBranches();
    List<Ref> branchesJGit = git.branchList().call();
    assertEquals(branchesGitData.size(), branchesJGit.size());
  }

  @Test
  void commitsAreSortedFromNewestToOldestTest() throws IOException, GitException {
    int size = 0;
    Iterator<GitCommit> it = gitData.getCommits();
    while (it.hasNext()) {
      size++;
      it.next();
    }
    assertEquals(COMMIT_DATA.length, size);
    it = gitData.getCommits();
    int i = size - 1;
    while (it.hasNext()) {
      assertEquals(new GitAuthor(COMMIT_DATA[i][0], COMMIT_DATA[i][1]), it.next().getAuthor());
      i--;
    }

  }

  @Test
  void commitsHaveCorrectCommitMessageTest() throws IOException, GitException {
    int i = COMMIT_DATA.length - 1;
    Iterator<GitCommit> it = gitData.getCommits();
    while (it.hasNext()) {
      assertEquals(COMMIT_DATA[i--][2],
          it.next().getMessage());
    }
  }
    @Test
    void reinitializeTest(){
        Git gitBegin = git;
        gitData.reinitialize();
        Git gitEnd = GitData.getJGit();
        assertEquals(gitBegin.getRepository().getDirectory(), gitEnd.getRepository().getDirectory());

    }

  @Test
  void findsAllBranches() throws GitAPIException, GitException {
    git.branchCreate().setName("Test1").call();
    git.branchCreate().setName("Test2").call();
    git.branchCreate().setName("Test3").call();

    List<GitBranch> branches = gitData.getBranches();
    assertEquals(4, branches.size()); // master + 3
  }

  @Test
  void getBranchesRepoTest() throws GitAPIException, GitException {
    String gitUrl = "https://github.com/rmccue/test-repository.git";
    deleteDir(repo);
    git = Git.cloneRepository()
        .setURI(gitUrl)
        .setDirectory(repo)
        .setCloneAllBranches(true)
        .setTransportConfigCallback(CredentialProviderHolder::configureTransport)
        .setCloneSubmodules(true)
        .call();
    List<GitRemote> remotes = gitData.getRemotes();
    assertEquals(1, remotes.size());
    GitRemote remote = remotes.iterator().next();
    List<GitBranch> branches = gitData.getBranches(remote);
    assertEquals(1, branches.size());
    assertEquals("master", branches.iterator().next().getName());
  }

  @Test
  void selectedBranchIsUpdated() throws GitAPIException, IOException, GitException {
    git.branchCreate().setName("Test1").call();
    git.branchCreate().setName("Test2").call();
    git.branchCreate().setName("Test3").call();

    List<GitBranch> branches = gitData.getBranches();
    GitBranch selected = branches.get(2); // arbitrary selection
    assertNotEquals(selected, gitData.getSelectedBranch());
    git.checkout().setName(selected.getFullName()).call();
    assertEquals(selected, gitData.getSelectedBranch());
  }
}

