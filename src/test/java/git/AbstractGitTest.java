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
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import settings.Settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

public abstract class AbstractGitTest {
    private static final String[][] COMMIT_DATA = {
            new String[]{"Tester 1", "tester1@example.com", "Commit 1"},
            new String[]{"Tester 2", "tester2@example.com", "Commit 2"},
            new String[]{"Tester 3", "tester3@example.com", "Commit 3"},
            new String[]{"Tester 4", "tester4@example.com", "Commit 4"},
    };
    @TempDir
    protected static File repo;
    protected GitData gitData;
    protected Git git;
    protected Repository repository;
    protected Settings settings;
    protected File textFile;

    protected boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete(); // The directory is empty now and can be deleted.
    }

    @BeforeEach
    protected void beforeEach() throws GitAPIException, IOException, GitException, URISyntaxException {
        deleteDir(repo);
        FileUtils.forceMkdir(repo);
        setupRepo();

        init();
    }

    @AfterEach
    void tearDown() {
        repository.close();
        git.close();
        GitData.getJGit().close();
        GitData.getRepository().close();

    }

    protected void setupRepo() throws GitAPIException, IOException {
        Git.init().setDirectory(repo).setBare(false).call();
        Git git = Git.open(repo);
        textFile = new File(repo.getPath() + "/textFile.txt");
        FileWriter fr = new FileWriter(textFile, true);
        fr.write("data 1");
        fr.flush();

        git.add().addFilepattern(textFile.getName()).call();
        git.commit().setCommitter(COMMIT_DATA[0][0], COMMIT_DATA[0][1]).setSign(false)
                .setMessage(COMMIT_DATA[0][2]).call();

        fr.write("data 2");
        fr.flush();

        git.add().addFilepattern(textFile.getName()).call();
        git.commit().setCommitter(COMMIT_DATA[1][0], COMMIT_DATA[1][1]).setSign(false)
                .setMessage(COMMIT_DATA[1][2]).call();

        fr.write("Neuer Inhalt des Files");
        fr.close();

        git.add().addFilepattern(textFile.getName()).call();
        git.commit().setCommitter(COMMIT_DATA[2][0], COMMIT_DATA[2][1]).setSign(false)
                .setMessage(COMMIT_DATA[2][2]).call();
        git.commit().setCommitter(COMMIT_DATA[3][0], COMMIT_DATA[3][1]).setSign(false)
                .setMessage(COMMIT_DATA[3][2]).call();

        git.close();
    }

    public void init() throws IOException, GitAPIException, GitException, URISyntaxException {
        settings = Settings.getInstance();
        settings.setActiveRepositoryPath(repo);
        gitData = new GitData();
        gitData.reinitialize();
        git = Git.open(repo);
        // Make sure user supplied signing settings dont crash the tests
        git.getRepository().getConfig().setBoolean("commit", null, "gpgsign", false);
        git.getRepository().getConfig().save();
        repository = git.getRepository();
        setAuthor();
    }

    private void setAuthor() throws IOException {
        StoredConfig config = git.getRepository().getConfig();
        config.setString("user", null, "name", "TestAuthor");
        config.setString("user", null, "email", "tester@example.com");
        config.save();
    }

}
