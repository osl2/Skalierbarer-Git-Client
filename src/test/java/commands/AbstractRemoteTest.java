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

import git.GitData;
import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractRemoteTest extends AbstractCommandTest {
  protected String REMOTE_URI = repo.getPath() + System.getProperty("file.separator") + ".git";
  protected static File remoteDir = new File("src" + System.getProperty("file.separator") + "test" +
          System.getProperty("file.separator") + "resources" + System.getProperty("file.separator")
          + "Test" + System.getProperty("file.separator") + "remote");

  @BeforeEach
  protected void createRemoteDir() throws IOException, GitAPIException, GitException {
    deleteDir(remoteDir.getAbsoluteFile());
    FileUtils.forceMkdir(remoteDir);
    Git result = Git.cloneRepository()
            .setURI(REMOTE_URI)
            .setDirectory(remoteDir)
            .setCloneAllBranches(true)
            .call();
    // You have to close this instance otherwise it is not possible on windows to delete this directory.
    result.getRepository().close();
    result.close();
    settings.setActiveRepositoryPath(remoteDir);
    gitData = new GitData();
    gitData.reinitialize();
    assertEquals(2, remoteDir.listFiles().length);
    assertEquals(1, gitData.getBranches().size());
  }

  @AfterEach
  protected void cleanUp() {
    deleteDir(remoteDir.getAbsoluteFile());
  }

}
