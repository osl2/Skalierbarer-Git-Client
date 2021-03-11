package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import settings.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GitFileTest extends AbstractGitTest {

  File fileNotStaged;
  GitFile gitFile;

  @BeforeEach
  void setup() {
    gitFile = new GitFile(fileNotStaged.getTotalSpace(), fileNotStaged);
  }


  @Override
  protected void setupRepo() throws GitAPIException, IOException {

    Git.init().setDirectory(repo).setBare(false).call();
    Git git = Git.open(repo);
    git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
            .setMessage("Commit 1").call();
    File textFile = new File(repo.getPath(), "textFile.txt");
    FileWriter fr = new FileWriter(textFile, true);
    fr.write("data");
    fr.close();
    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 2", "tester2@example.com").setSign(false)
            .setMessage("Commit 2").call();
    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 3", "tester3@example.com").setSign(false)
            .setMessage("Commit 3").call();
    git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
            .setMessage("Commit 4").call();
    File fileNotStaged = new File(repo.getPath(), "textFile3.txt");
    FileWriter fr3 = new FileWriter(fileNotStaged);
    fr3.write("Neuer Inhalt des Files");
    fr3.close();
    this.fileNotStaged = fileNotStaged;

    git.close();
  }


  @Test
  void addTest() throws GitException, GitAPIException, IOException {
    gitFile.add(); //  adding first file
    String gitDataFile = gitData.getStatus().getAddedFiles().iterator().next().getPath().getName();
    String jGitFile = git.status().call().getAdded().iterator().next();
    assertEquals(gitDataFile, jGitFile);
    assertTrue(GitStatus.getInstance().getAddedFiles().contains(gitFile));
    assertTrue(git.status().call().getAdded().contains(gitFile.getPath().getName()));
    File newFolder = new File(repo, "newFolder");
    assertTrue(newFolder.mkdir());
    File newFile = new File(newFolder.getPath(), "newFile.txt");
    FileWriter fr = new FileWriter(newFile, true);
    fr.write("data");
    fr.close();
    GitFile toAdd = new GitFile(newFile.getTotalSpace(), newFile);
    toAdd.add(); //adding second File
    assertEquals(2, git.status().call().getAdded().size());

  }

  @Test
  void addUndoTest() throws GitException, GitAPIException {
    git.add().addFilepattern(repo.toPath().relativize(fileNotStaged.toPath()).toString()).call();
    Status status = git.status().call();
    Set<String> addedFiles = status.getAdded();
    assertTrue(addedFiles.contains(fileNotStaged.getName()));
    gitFile.addUndo();
    status = git.status().call();
    addedFiles = status.getAdded();
    assertFalse(addedFiles.contains(fileNotStaged.getPath()));
    assertTrue(addedFiles.isEmpty());
  }

  @Test
  void newGitFileInvalidPathTest() {
    String absolutePath = Settings.getInstance().getActiveRepositoryPath().getAbsolutePath();
    File testFile = new File("wrong" + absolutePath + "\\test\\testfile.txt");
    long space = testFile.getTotalSpace();
    assertThrows(AssertionError.class, () -> new GitFile(space, testFile));
  }

  @Test
  void getSizeTest() {
    assertEquals(fileNotStaged.getTotalSpace(), gitFile.getSize());
  }

  @Test
  void isStagedTest() throws GitAPIException, GitException, IOException {
    assertFalse(gitFile.isStaged());
    git.add().addFilepattern(repo.toPath().relativize(fileNotStaged.toPath()).toString()).call();
    assertTrue(gitFile.isStaged());
    git.commit().setMessage("Test").setCommitter("Testuser", "tester@example.com").call();
    assertFalse(gitFile.isStaged());
    FileOutputStream out = new FileOutputStream(fileNotStaged);
    out.write("Test".getBytes(StandardCharsets.UTF_8));
    out.close();
    git.add().addFilepattern(repo.toPath().relativize(fileNotStaged.toPath()).toString()).call();
    assertTrue(gitFile.isStaged());
  }

  @Test
  void isDeletedTest() throws GitException, GitAPIException {
    git.add().addFilepattern(repo.toPath().relativize(fileNotStaged.toPath()).toString()).call();

    assertFalse(gitFile.isDeleted());

    git.commit().setMessage("Test").setCommitter("Testuser", "tester@example.com").call();
    assertTrue(fileNotStaged.delete());
    assertTrue(gitFile.isDeleted());
    git.add().addFilepattern(repo.toPath().relativize(fileNotStaged.toPath()).toString()).call();
    assertTrue(gitFile.isDeleted());
  }

  @Test
  void removedFileIsDeletedTest() throws GitAPIException, GitException {
    git.add().addFilepattern(repo.toPath().relativize(fileNotStaged.toPath()).toString()).call();
    assertFalse(gitFile.isDeleted());

    git.commit().setMessage("Test").setCommitter("Testuser", "tester@example.com").call();

    git.rm().addFilepattern(repo.toPath().relativize(fileNotStaged.toPath()).toString()).call();
    assertTrue(gitFile.isDeleted());
  }

  @Test
  void equalsTest() {
    File file2 = fileNotStaged;
    GitFile gitFile2 = new GitFile(file2.getTotalSpace(), file2);
    assertEquals(gitFile2, gitFile);

    File file3 = new File(repo.getPath(), "textFile4.txt");
    GitFile gitFile3 = new GitFile(file3.getTotalSpace(), file3);
    assertNotEquals(gitFile3, gitFile);

  }

  @Test
  void hashCodeTest() {
    File file2 = fileNotStaged;
    GitFile gitFile2 = new GitFile(fileNotStaged.getTotalSpace(), fileNotStaged);
    assertEquals(gitFile.hashCode(), gitFile2.hashCode());
  }
}
