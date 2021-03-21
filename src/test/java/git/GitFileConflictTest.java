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

import git.conflict.ConflictHunk;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GitFileConflictTest extends AbstractGitTest {
    List<GitFileConflict> conflictsForWorkingDirectory;

    @Override
    public void init() throws IOException, GitAPIException, GitException, URISyntaxException {
        super.init();
        Iterable<RevCommit> log = git.log().setSkip(3).setMaxCount(1).call();
        git.branchCreate().setName("merge-test").setStartPoint(log.iterator().next()).call();
        git.checkout().setName("merge-test").call();
        FileWriter fw = new FileWriter(textFile, false);
        fw.write("Overwrote file");
        fw.flush();
        fw.close();
        git.add().addFilepattern(textFile.getName()).call();
        git.commit().setCommitter("editor", "editor@example.com").setSign(false)
                .setMessage("Prepare changes for merge").call();

        git.checkout().setName("master").call();

        // Now we force a merge.

        Iterator<GitBranch> branchIterator = gitData.getBranches().iterator();
        GitBranch src = null;
        while (branchIterator.hasNext()) {
            src = branchIterator.next();
            if (src.getName().equals("merge-test")) break;
            else src = null;
        }

        assertNotNull(src);
        src.merge(false);

        conflictsForWorkingDirectory = GitFileConflict.getConflictsForWorkingDirectory();

    }

    @Test
    void conflictsAreFoundTest() {
        assertEquals(1, conflictsForWorkingDirectory.size());
        GitFileConflict fileConflict = conflictsForWorkingDirectory.get(0);
        assertEquals(1, fileConflict.getConflictHunkList().size());
        assertEquals(textFile, fileConflict.getGitFile().getPath());
        assertTrue(fileConflict.getHunkList().containsAll(fileConflict.getConflictHunkList()));
    }

    @Test
    void ownershipTest() {
        ConflictHunk textConflict = conflictsForWorkingDirectory.get(0).getConflictHunkList().get(0);
        String masterString = "data 1data 2Neuer Inhalt des Files";
        String testString = "Overwrote file";

        assertArrayEquals(new String[]{masterString}, textConflict.getOurs());
        assertArrayEquals(new String[]{testString}, textConflict.getTheirs());
    }

    @Test
    void applyTest() throws IOException, GitException {
        String testString = "Overwrote file";
        GitFileConflict fileConflict = conflictsForWorkingDirectory.get(0);
        assertFalse(fileConflict.apply()); // unresolved Hunks

        fileConflict.getConflictHunkList().forEach(ConflictHunk::acceptTheirs);
        assertFalse(fileConflict.wasDeleted());
        assertTrue(fileConflict.apply());

        BufferedReader fr = new BufferedReader(new FileReader(textFile));

        String[] fileContents = fr.lines().toArray(String[]::new);

        assertArrayEquals(new String[]{testString}, fileContents);


    }
}
