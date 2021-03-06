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

import commands.AbstractCommandTest;
import commands.Add;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AddTest extends AbstractCommandTest {
    private File file1;
    private File file2;
    private File file3;
    private GitFile gitFile1;
    private GitFile gitFile2;
    private GitFile gitFile3;
    private Add add;
    private List<GitFile> files;

    @BeforeEach
    void prepare() throws IOException {
        file1 = new File(repo, "file1");
        file2 = new File(repo, "file2");
        file3 = new File(repo, "file3");
        new FileOutputStream(file1).close();
        new FileOutputStream(file2).close();
        new FileOutputStream(file3).close();
        gitFile1 = new GitFile(file1.getTotalSpace(), file1);
        gitFile2 = new GitFile(file2.getTotalSpace(), file2);
        gitFile3 = new GitFile(file3.getTotalSpace(), file3);

        add = new Add();
        files = new ArrayList<>();
    }


    @Test
    void executeAddTest() throws GitAPIException {
        files.add(gitFile2);
        files.add(gitFile3);
        add.setSelectedFiles(files);

        //add file1 and file2 manually
        git.add()
                .addFilepattern(repo.toPath().relativize(file1.toPath()).toString())
                .addFilepattern(repo.toPath().relativize(file2.toPath()).toString())
                .call();
        Set<String> addedFiles = git.status().call().getAdded();
        assertTrue(addedFiles.contains(file2.getName()));

        //execute add
        add.execute();

        //file2 and file3 should now be added, file1 should have been removed from the
        // staging area, list of added files should contain 2 elements
        addedFiles = git.status().call().getAdded();
        assertFalse(addedFiles.contains(file1.getName()));
        assertTrue(addedFiles.contains(file2.getName()));
        assertTrue(addedFiles.contains(file3.getName()));
        assertEquals(2, addedFiles.size());


    }

    @Test
    void getFilesStatusChangedTest() throws GitAPIException {
        //configure add command with file2 and file3 that have been selected
        files.add(gitFile2);
        files.add(gitFile3);
        add.setSelectedFiles(files);

        //add file1 and file2 manually
        git.add()
                .addFilepattern(repo.toPath().relativize(file1.toPath()).toString())
                .addFilepattern(repo.toPath().relativize(file2.toPath()).toString())
                .call();
        assertTrue(git.status().call().getAdded().contains(file1.getName()));
        assertTrue(git.status().call().getAdded().contains(file2.getName()));



        /*get files whose status changed. Status of a file can have either changed because the file was selected by the
        user and therefore added, or because the file was deselected and therefore unstaged
         */
        assertTrue(add.getFilesStatusChanged().contains(gitFile1));
        assertFalse(add.getFilesStatusChanged().contains(gitFile2));
        assertTrue(add.getFilesStatusChanged().contains(gitFile3));
    }

    @Test
    void addDeletedFileTest() throws GitAPIException {
        git.add()
                .addFilepattern(repo.toPath().relativize(file1.toPath()).toString())
                .call();
        assertTrue(git.status().call().getAdded().contains(file1.getName()));
        git.commit().setCommitter("TestUser", "Tester@example.com").setMessage("Test message").call();
        assertFalse(git.status().call().getAdded().contains(file1.getName()));

        //delete file. File should be missing but not removed, because it was deleted manually
        assertTrue(file1.delete());
        assertTrue(git.status().call().getMissing().contains(file1.getName()));
        assertFalse(git.status().call().getRemoved().contains(file1.getName()));

        //prepare add command to add deleted file
        files.add(gitFile1);
        add.setSelectedFiles(files);
        assertTrue(add.execute());

        //file should now be removed
        assertFalse(git.status().call().getMissing().contains(file1.getName()));
        assertTrue(git.status().call().getRemoved().contains(file1.getName()));
    }

    @Test
    void addNestedFileTest() throws GitException, GitAPIException, IOException {
        File dir = new File(repo, "dir");
        assert dir.mkdir();
        File nestedFile = new File(dir, "nestedFile");
        new FileOutputStream(nestedFile).close();
        GitFile nestedGitFile = new GitFile(nestedFile.getTotalSpace(), nestedFile);

        files.add(nestedGitFile);
        add.setSelectedFiles(files);
        add.execute();
        assertTrue(nestedGitFile.isStaged());
        assertTrue(git.status().call().getAdded().contains("dir/nestedFile"));
    }

    /*
    This testcase corresponds partly to global testcase 26 from the Pflichtenheft
     */
    @Test
    void globalCommandLineTest_T26() throws IOException {
        File dir = new File(repo, "dir");
        assert dir.mkdir();
        File nestedFile = new File(dir, "nestedFile");
        new FileOutputStream(nestedFile).close();
        GitFile nestedGitFile = new GitFile(nestedFile.getTotalSpace(), nestedFile);
        files.add(gitFile1);
        files.add(gitFile2);
        files.add(nestedGitFile);
        add.setSelectedFiles(files);
        String commandLine = add.getCommandLine();
        assertTrue(commandLine.isEmpty());

        //execute add
        add.execute();

        //command line should now represent the command that was just executed
        commandLine = add.getCommandLine();
        String separator = File.separator.compareTo("/") == 0 ? "/" : "\\";
        assertEquals(0, commandLine.compareTo("git add file1 file2 dir" + separator + "nestedFile "));
    }

    @Test
    void getDescriptionTest() {
        Add add = new Add();
        assertNotNull(add.getDescription());
    }

    @Test
    void onButtonClickedTest() {
        add.onButtonClicked();

        //on button clicked should have opened AddCommitView
        assertTrue(guiControllerTestable.openViewCalled);

    }
}
