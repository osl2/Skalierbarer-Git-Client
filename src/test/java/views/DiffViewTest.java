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
package views;

import commands.AbstractCommandTest;
import git.GitCommit;
import git.GitFile;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.revwalk.*;
import org.junit.jupiter.api.Test;
import shaded.org.apache.commons.lang3.*;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiffViewTest extends AbstractCommandTest {

  @Test
  void testFalseInputSetDiffFile() {
    DiffView diffView = new DiffView();
    diffView.setDiff(null);
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
  }

  @Test
  void testFalseInputSetDiffCommitAndFile() {
    DiffView diffView = new DiffView();
    diffView.setDiff(null, null);
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
  }

  @Test
  void testDiffOutput() throws IOException, GitException {
    Iterator<GitCommit> it = gitData.getCommits();
    DiffView diffView = new DiffView();
    while (it.hasNext()) {
      GitCommit commit = it.next();
      if (commit.getMessage().compareTo("Commit 2") == 0) {
        List<GitFile> file = commit.getChangedFiles();
        assertEquals(1, file.size());
        diffView.setDiff(commit, file.get(0));
        JTextPane pane = diffView.openDiffView();
        String text = pane.getText();
        ArrayList<String> lines = new ArrayList<>();
        text.lines().forEach(lines::add);
        assertEquals("@@ -1 +1 @@", lines.get(0));
        // Check if changed or staged files produce an empty output to avoid Exceptions.
        diffView.setDiff(file.get(0));
        assertEquals("", pane.getText());
        diffView.setNotVisible();
        assertEquals("", pane.getText());
      }
    }
  }

  @Test
  void messageLengthTest() throws IOException, GitException, GitAPIException {
    //Make a commit with a long commit-message

    String message = RandomStringUtils.randomAlphanumeric((100^100));
    FileWriter fr = new FileWriter(textFile, true);
    fr.write("data 2");
    fr.close();
    git.add().addFilepattern(textFile.getName()).call();
    RevCommit test = git.commit().setMessage(message).call();
    assertEquals(message, test.getFullMessage());
    assertTrue(test.getFullMessage().contains(test.getShortMessage()));


    Iterator<GitCommit> it = gitData.getCommits();
    DiffView diffView = new DiffView();
    while (it.hasNext()) {
      GitCommit commit = it.next();
      if (commit.getMessage().compareTo(message) == 0) {
        List<GitFile> file = commit.getChangedFiles();
        assertEquals(1, file.size());
      }
    }
  }
}
