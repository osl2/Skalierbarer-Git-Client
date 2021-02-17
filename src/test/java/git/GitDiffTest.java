package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitDiffTest extends AbstractGitTest {

  @Override
  protected void setupRepo() throws GitAPIException, IOException {
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
  public void getDiffTest() throws IOException, GitException {
    Iterator<GitCommit> commits = gitData.getCommits();
    GitCommit commit;
    while(commits.hasNext()) {
      GitCommit commitselect = commits.next();
      if(commitselect.getMessage().equals("Commit 1")) {
        commit = commitselect;
        String out = commit.getDiff(null, commit.getChangedFiles().get(0));
        ArrayList<String> lines = new ArrayList<String>();
        out.lines().forEach(lines::add);
        assertEquals("@@ -0,0 +1 @@", lines.get(5));
        assertEquals("+data 1", lines.get(6));
      } else if (commitselect.getMessage().equals("Commit 2")) {
        commit = commitselect;
        String out = commit.getDiff(commit.getParents()[0], commit.getChangedFiles().get(0));
        ArrayList<String> lines = new ArrayList<String>();
        out.lines().forEach(lines::add);
        assertEquals("@@ -1 +1 @@", lines.get(4));
        assertEquals("-data 1", lines.get(5));
        assertEquals("+data 1data 2", lines.get(7));
      } else if (commitselect.getMessage().equals("Commit 3")) {
        commit = commitselect;
        String out = commit.getDiff(commit.getParents()[0], commit.getChangedFiles().get(0));
        ArrayList<String> lines = new ArrayList<String>();
        out.lines().forEach(lines::add);
        assertEquals("@@ -1 +1 @@", lines.get(4));
        assertEquals("-data 1data 2", lines.get(5));
        assertEquals("+data 1data 2Neuer Inhalt des Files", lines.get(7));
      }
    }
  }

  @Test
  public void getDiffWorkingDirectory() throws IOException {
    String out = GitCommit.getDiff(new GitFile(20, new File(repo, "/textFile.txt")));
    ArrayList<String> lines = new ArrayList<String>();
    out.lines().forEach(lines::add);
    assertEquals("-data 1data 2Neuer Inhalt des Files", lines.get(5));
    assertEquals("+data 1data 2Neuer Inhalt des FilesNicht gestaged", lines.get(7));
  }
}
