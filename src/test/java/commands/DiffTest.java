package commands;

import git.GitCommit;
import git.GitFile;
import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiffTest extends AbstractCommandTest {
  private Diff diff;

  @Override
  protected void setupRepo() throws GitAPIException, IOException {
    diff = new Diff();
    Git.init().setDirectory(repo).setBare(false).call();
    Git git = Git.open(repo);
    File textFile = new File(repo.getPath() + "/textFile.txt");
    FileWriter fr = new FileWriter(textFile, true);
    fr.write("data 1");
    fr.flush();

    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
            .setMessage("Commit 1").call();

    fr.write("data 2");
    fr.flush();

    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 2", "tester2@example.com").setSign(false)
            .setMessage("Commit 2").call();

    fr.write("Neuer Inhalt des Files");
    fr.flush();

    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 3", "tester3@example.com").setSign(false)
            .setMessage("Commit 3").call();
    git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
            .setMessage("Commit 4").call();

    fr.write("Nicht gestaged");
    fr.close();
    git.close();
  }

  @Test
  void testInvalidInput() {
    assertFalse(diff.execute());
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    assertEquals(1, diff.diffGit().length);
    assertEquals("", diff.diffGit()[0]);
  }

  @Test
  void testDiffWorkingDirectory() throws GitException {
    List<GitFile> fileList = gitData.getStatus().getModifiedFiles();
    assertEquals(1, fileList.size());
    diff.setDiffFile(fileList.get(0));
    assertTrue(diff.execute());
    String[] out = diff.diffGit();
    assertTrue(out[0].startsWith("@@"));
    assertEquals("-data 1data 2Neuer Inhalt des Files", out[1]);
    assertEquals("+data 1data 2Neuer Inhalt des FilesNicht gestaged", out[3]);
  }

  @Test
  void testDiffFirstCommitToEmptyRepository() throws IOException, GitException {
    Iterator<GitCommit> commits = gitData.getCommits();
    while (commits.hasNext()) {
      GitCommit commitSelect = commits.next();
      if (commitSelect.getMessage().equals("Commit 1")) {
        List<GitFile> list = commitSelect.getChangedFiles();
        assertEquals(1, list.size());
        diff.setDiffCommit(commitSelect, list.get(0));
        assertTrue(diff.execute());
        String[] out = diff.diffGit();
        assertTrue(out[0].startsWith("@@"));
        assertEquals("+data 1", out[1]);
      }
    }
  }

  @Test
  void testDiffCommitToPreviousOne() throws IOException, GitException {
    Iterator<GitCommit> commits = gitData.getCommits();
    while (commits.hasNext()) {
      GitCommit commitSelect = commits.next();
      if (commitSelect.getMessage().equals("Commit 2")) {
        List<GitFile> list = commitSelect.getChangedFiles();
        assertEquals(1, list.size());
        diff.setDiffCommit(commitSelect, list.get(0));
        assertTrue(diff.execute());
        String[] out = diff.diffGit();
        assertTrue(out[0].startsWith("@@"));
        assertEquals("-data 1", out[1]);
        assertEquals("+data 1data 2", out[3]);
      }
    }
  }
}
