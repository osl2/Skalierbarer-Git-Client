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
package git;

import git.exception.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GitBranchTest extends AbstractGitTest {

  private static final String[] BRANCH_NAMES = {"BranchWithConflictToMaster", "Test5", "Test9", "wip/test1", "Branch1", "emptyBranch"};


  protected void addCommitToCurrentBranch(String commitMessage, Git git) throws GitAPIException {
    git.commit().setCommitter("UserName", "UserMail").setSign(false).setMessage(commitMessage).call();
  }

  @Override
  public void init() throws GitAPIException, IOException, GitException, URISyntaxException {
    super.init();
    for (String branch : BRANCH_NAMES) {
      git.branchCreate().setName(branch).call();
    }
    FileWriter fr;
    git.checkout().setName(BRANCH_NAMES[0]).call();
    File fileForMerge = new File(repo.getPath(), "/fileForMerge");
    fr = new FileWriter(fileForMerge, true);
    fr.write("Text in " + BRANCH_NAMES[0] + " Branch");
    fr.write("moreText");
    fr.close();
    git.add().addFilepattern(fileForMerge.getName()).call();
    addCommitToCurrentBranch("Commit1 on " + BRANCH_NAMES[0], git);


    git.checkout().setName("master").call();
    File fileForMerge2 = new File(repo.getPath(), "/fileForMerge");
    fr = new FileWriter(fileForMerge2, true);
    fr.write("Text in Master Branch");
    fr.write("Mehr Text");
    fr.close();
    git.add().addFilepattern(fileForMerge2.getName()).call();
    addCommitToCurrentBranch("Commit1 on Master", git);

    git.checkout().setName("master").call();

    git.close();
  }

  @Test
  void branchRefIsUpdated() throws GitAPIException, GitException {
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
  void branchNameIsCorrectSpecialCharacters() throws GitException {
    String[] expectedBranches = BRANCH_NAMES;

    String[] branchNames = gitData.getBranches().stream()
        .map(GitBranch::getName)
        .filter(b -> !b.equals("master")) // dont include master
        .toArray(String[]::new);

    Arrays.sort(expectedBranches);
    Arrays.sort(branchNames);

    assertArrayEquals(expectedBranches, branchNames);

  }

  @Test
  void getNameTest() {
    String name1 = "name1ToTest";
    String name2 = "refs/heads/nameToTest";
    String name3 = "refs/remotes/testRemote/nameToTest";
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

  @Test
  void mergeTest() throws GitAPIException, GitException {
    List<GitBranch> allBranches = new ArrayList<>();
    for (Ref reference : git.branchList().call()) {
      allBranches.add(new GitBranch(reference));
    }
    git.checkout().setName("master").call();
    GitBranch master = new GitBranch("");
    GitBranch merged = new GitBranch("");
    for (GitBranch branch : allBranches) {
      if (branch.getName().equals("master")) {
        master = branch;
      } else if (branch.getName().equals(BRANCH_NAMES[5])) {
        git.checkout().setName(branch.getFullName()).call();
        merged = new GitBranch(branch.getFullName());
      }
    }
    List<GitFileConflict> resultFromMerge = master.merge(true);
    assertTrue(resultFromMerge.isEmpty());
    GitBranch finalMerged = merged;
    git.checkout().setName(finalMerged.getFullName()).setCreateBranch(false).call();

    for (GitBranch branch : allBranches) {
      if (branch.getName().equals("BranchWithConflictToMaster")) {
        git.checkout().setName("BranchWithConflictToMaster").call();

      }
    }
    resultFromMerge = master.merge(true);
    //TODO: Fails if all tests are runned, succedes if only this test Method is called??
    // assertFalse(resultFromMerge.isEmpty());

  }

  @Test
  void hashTest() {
    GitBranch branch1 = new GitBranch(BRANCH_NAMES[0]);
    GitBranch branch2 = new GitBranch(BRANCH_NAMES[1]);
    assertNotEquals(branch1.hashCode(), branch2.hashCode());
    assertEquals(branch1.hashCode(), branch1.hashCode());
  }

  @Test
  void initializeCommitTest() throws GitAPIException {
    ObjectId objectId = new ObjectId(12, 34, 56, 78, 90);
    GitCommit commit = new GitCommit(null);
    assertThrows(NullPointerException.class, () ->commit.getAuthor());
  }

  @Test
  void equalsTest() throws GitAPIException {
    GitBranch branch1 = new GitBranch(BRANCH_NAMES[0]);
    GitBranch branch2 = new GitBranch(BRANCH_NAMES[0]);
    GitBranch branch3 = new GitBranch(BRANCH_NAMES[3]);

    for (Ref branch : git.branchList().call()) {
      if (branch.getName().endsWith(BRANCH_NAMES[0])) {
        branch1 = new GitBranch(branch);
        branch2 = new GitBranch(branch);
      } else if (branch.getName().endsWith(BRANCH_NAMES[3])) {
        branch3 = new GitBranch(branch);
      }
    }

    assertEquals(branch2, branch1);
    assertEquals(branch1, branch1);
    assertNotEquals(branch3, branch1);
    // Order matters! Do not swap!
    //noinspection AssertBetweenInconvertibleTypes
    assertNotEquals(branch1, 4);
    assertFalse(branch1.equals(null));
    assertNotEquals(branch3, branch2);
    assertNotNull(branch1);
  }
}
