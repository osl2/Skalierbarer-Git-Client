package git;

import commands.AbstractCommandTest;
import commands.Add;
import controller.GUIController;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class AddTest extends AbstractCommandTest {
    @Test
    void executeAddTest() throws IOException, GitAPIException, GitException {
        File file1 = new File(repo, "file1");
        File file2 = new File(repo, "file2");
        File file3 = new File(repo, "file3");
        new FileOutputStream(file1).close();
        new FileOutputStream(file2).close();
        new FileOutputStream(file3).close();
        GitFile gitFile1 = new GitFile(file1.getTotalSpace(), file1);
        GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);
        GitFile gitFile3 = new GitFile(file3.getTotalSpace(), file3);

        //prepare add command to add file2 and file3
        Add add = new Add();
        List<GitFile> files = new LinkedList<>();

        files.add(gitFile2);
        files.add(gitFile3);
        add.setFiles(files);

        //add file1 and file2 manually
        git.add()
                .addFilepattern(repo.toPath().relativize(file1.toPath()).toString())
                .addFilepattern(repo.toPath().relativize(file2.toPath()).toString())
                .call();
        List<GitFile> addedFiles = GitStatus.getInstance().getAddedFiles();
        assertTrue(addedFiles.contains(gitFile2));

        //execute add
        add.execute();

        //file2 and file3 should now be added, file1 should have been removed from the
        // staging area, list of added files should contain 2 elements
        addedFiles = GitStatus.getInstance().getAddedFiles();
        assertFalse(addedFiles.contains(gitFile1));
        assertTrue(addedFiles.contains(gitFile2));
        assertTrue(addedFiles.contains(gitFile3));
        assertEquals(2, addedFiles.size());


    }

    @Test
    void getFilesStatusChangedTest() throws IOException, GitAPIException, GitException {
        File file1 = new File(repo, "file1");
        File file2 = new File(repo, "file2");
        File file3 = new File(repo, "file3");
        new FileOutputStream(file1).close();
        new FileOutputStream(file2).close();
        new FileOutputStream(file3).close();
        GitFile gitFile1 = new GitFile(file2.getTotalSpace(), file1);
        GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);
        GitFile gitFile3 = new GitFile(file3.getTotalSpace(), file3);

        //prepare add command to add file2 and file3
        Add add = new Add();

        //configure add command with file2 and file3 that have been selected
        List<GitFile> files = new LinkedList<>();
        files.add(gitFile2);
        files.add(gitFile3);
        add.setFiles(files);

        //add file1 and file2 manually
        git.add()
                .addFilepattern(repo.toPath().relativize(file1.toPath()).toString())
                .addFilepattern(repo.toPath().relativize(file2.toPath()).toString())
                .call();
        assertTrue(GitStatus.getInstance().getStagedFiles().contains(gitFile1));
        assertTrue(GitStatus.getInstance().getStagedFiles().contains(gitFile2));
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
    void addDeletedFileTest() throws IOException, GitAPIException {
        File file = new File(repo, "file");
        new FileOutputStream(file).close();
        git.add()
                .addFilepattern(repo.toPath().relativize(file.toPath()).toString())
                .call();
        assertTrue(git.status().call().getAdded().contains(file.getName()));
        git.commit().setCommitter("TestUser", "123@web.de").setMessage("Test message").call();
        assertFalse(git.status().call().getAdded().contains(file.getName()));

        //delete file. File should be missing but not removed, because it was deleted manually
        assertTrue(file.delete());
        assertTrue(git.status().call().getMissing().contains(file.getName()));
        assertFalse(git.status().call().getRemoved().contains(file.getName()));

        //prepare add command to add deleted file
        Add add = new Add();
        GitFile gitFile = new GitFile(file.getTotalSpace(), file);
        gitFile.setDeleted(true);
        List<GitFile> files = new LinkedList<>();
        files.add(gitFile);
        add.setFiles(files);
        assertTrue(add.execute());

        //file should now be removed
        assertFalse(git.status().call().getMissing().contains(file.getName()));
        assertTrue(git.status().call().getRemoved().contains(file.getName()));
    }

    @Test
    void getCommandLineTest() {
        File file1 = new File(repo, "file1");
        File file2 = new File(repo, "file2");
        File dir = new File(repo, "dir");
        assert dir.mkdir();
        File file3 = new File(dir, "file3");
        GitFile gitFile1 = new GitFile(file1.getTotalSpace(), file1);
        GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);
        GitFile gitFile3 = new GitFile(file3.getTotalSpace(), file3);
        Add add = new Add();
        List<GitFile> files = new ArrayList<>();
        files.add(gitFile1);
        files.add(gitFile2);
        files.add(gitFile3);
        add.setFiles(files);
        String commandLine = add.getCommandLine();
        String separator = File.separator.compareTo("/") == 0 ? "/" : "\\";
        assertEquals(0, commandLine.compareTo("git add file1 file2 dir" + separator + "file3 "));
    }

    @Test
    void getDescriptionTest() {
        Add add = new Add();
        assertEquals(0, add.getDescription().compareTo("Fügt Dateien zur Staging-Area hinzu"));
    }

    @Test
    void onButtonClickedTest() {
        Add add = new Add();
        GUIControllerTestable guiControllerTestable = new GUIControllerTestable();
        MockedStatic<GUIController> mockedController = mockStatic(GUIController.class);
        mockedController.when(GUIController::getInstance).thenReturn(guiControllerTestable);
        guiControllerTestable.resetTestStatus();
        add.onButtonClicked();

        //on button clicked should have opened AddCommitView
        assertTrue(guiControllerTestable.openViewCalled);

        mockedController.close();

    }
}
