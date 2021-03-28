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

import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitRemoteTest extends AbstractGitTest {

  GitRemote gitRemote;

  @Override
  public void init() throws URISyntaxException, GitAPIException, GitException, IOException {
    super.init();
    resetRemote();
  }

  private void resetRemote() {
    gitRemote = new GitRemote("url", "name");
  }

  @Test
  void getterAndSetterTest() throws GitException {
    assertEquals(new GitRemote("url", "name"), gitRemote);
    resetRemote();
    gitRemote.setName("newName");
    assertEquals(new GitRemote("url", "newName"), gitRemote);
    assertEquals("newName", gitRemote.getName());
    resetRemote();
    assertTrue(gitRemote.setUrl("newUrl"));
    assertEquals(new GitRemote("newUrl", "name"), gitRemote);
  }

  @Test
  void equalsTest() {
    assertTrue(gitRemote.equals(gitRemote));
    assertTrue(gitRemote.equals(new GitRemote("url", "name")));
  }
}
