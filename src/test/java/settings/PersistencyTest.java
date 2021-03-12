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
package settings;

import git.AbstractGitTest;
import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class PersistencyTest extends AbstractGitTest {
    @TempDir
    static File config;
    private static final File TESTFILE = new File("testdir");
    Data data;

    @Override
    public void init() throws IOException, GitAPIException, URISyntaxException, GitException {
        super.init();
        data = Data.getInstance();
        settings.setLevel(data.getLevels().get(0));
        settings.setUser(gitData.getCommits().next().getAuthor());
        settings.setShowTreeView(false);
        settings.setUseTooltips(true);
        data.storeNewRepositoryPath(TESTFILE);
        deleteDir(config);
        FileUtils.forceMkdir(config);
        new Persistency().save(config);
    }

    @Test
    void singletonsAreCreatedCorrectlyTest() throws IllegalAccessException, URISyntaxException {
        // The Instance fields are private, so we should not be able to set them. We use reflection to still do that.
        // This should create new instances when we try to get another Settings or Data instance.
        FieldUtils.writeField(settings, "INSTANCE", null, true);
        FieldUtils.writeField(data, "INSTANCE", null, true);

        new Persistency().load(config);
        Data newData = Data.getInstance();
        Settings newSettings = Settings.getInstance();

        // If those fail, we did not get a new instance.
        assertNotSame(data, newData);
        assertNotSame(settings, newSettings);

        assertEquals(settings.getLevel(), newSettings.getLevel());
        assertEquals(settings.getActiveRepositoryPath(), newSettings.getActiveRepositoryPath());

        assertEquals(data.getLevels(), newData.getLevels());
        assertEquals(data.getRecentlyOpenedRepositories(), newData.getRecentlyOpenedRepositories());
    }

    @Test
    void directoriesAreOnlyAddedOnceTest() {
        List<File> recentlyOpenedRepositories = data.getRecentlyOpenedRepositories();
        data.storeNewRepositoryPath(TESTFILE.getAbsoluteFile());
        assertEquals(recentlyOpenedRepositories, data.getRecentlyOpenedRepositories());
    }

}
