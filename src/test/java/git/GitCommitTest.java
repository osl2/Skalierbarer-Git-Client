package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GitCommitTest extends AbstractGitTest {

  @Override
  protected void setupRepo() throws GitAPIException, IOException {
    Git.init().setDirectory(repo).setBare(false).call();
    Git git = Git.open(repo);
    File textFile = new File(repo.getPath() + "/textFile.txt");
    FileWriter fr = new FileWriter(textFile, true);

    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
        .setMessage("Commit 1").call();

    fr.write("data");
    fr.flush();

    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 2", "tester2@example.com").setSign(false)
        .setMessage("Commit 2").call();

    fr.write("Neuer Inhalt des Files");
    fr.close();

    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 3", "tester3@example.com").setSign(false)
        .setMessage("Commit 3").call();
    git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
        .setMessage("Commit 4").call();

    git.close();
  }

  @Test
  void getChangedFilesTest() throws IOException, GitException {
    Iterator<GitCommit> commits = gitData.getCommits();
    GitCommit commit;
    while (commits.hasNext()) {
      GitCommit commitselect = commits.next();
      if (commitselect.getMessage().equals("Commit 2")) {
        commit = commitselect;
        List<GitFile> changedFiles = commit.getChangedFiles();
        assertEquals(changedFiles.size(), 1);
        GitFile file = changedFiles.get(0);
        assertEquals(new File(repo, "textFile.txt"), file.getPath());
      }
    }

  }

  @Test
  void getChangedFilesFirstCommitTest() throws IOException, GitException {
    Iterator<GitCommit> commits = gitData.getCommits();
    GitCommit commit;
    while (commits.hasNext()) {
      GitCommit commitselect = commits.next();
      if (commitselect.getMessage().equals("Commit 1")) {
        commit = commitselect;
        List<GitFile> changedFiles = commit.getChangedFiles();
        assertEquals(changedFiles.size(), 1);
        GitFile file = changedFiles.get(0);
        assertEquals(new File(repo, "textFile.txt"), file.getPath());
      }
    }
  }

  @Test
  void isSignedTest() throws GitAPIException, IOException, GitException {
    RevCommit jGitCommit = git.log().setMaxCount(2).call().iterator().next();
    GitCommit commit = new GitCommit(jGitCommit);
    assertFalse(commit.isSigned());
    File textFile = new File(repo, "textFile.txt");
    FileWriter fr = new FileWriter(textFile, true);
    fr.write("newText");
    fr.close();
    GitFile gitFile = new GitFile(textFile.getTotalSpace(), textFile);
    git.add().addFilepattern(gitFile.getRelativePath()).call();
    RevCommit rev = git.commit().setSign(false).setMessage("newestCommit").call();
    GitCommit newCommit = new GitCommit(rev);
    assertFalse(newCommit.isSigned());


  }


  @Test
  void dgetDiffTest() throws GitAPIException, IOException {
    Iterable<RevCommit> log = git.log().call();
    RevCommit dummy = log.iterator().next();
    GitCommit commit4 = new GitCommit(dummy);
    GitCommit commit3 = new GitCommit(dummy);
    GitCommit commit2 = new GitCommit(dummy);
    GitCommit commit1 = new GitCommit(dummy);
    for (RevCommit rev : log) {
      if (rev.getFullMessage().endsWith("4")) {
        commit4 = new GitCommit(rev);
      } else if (rev.getFullMessage().endsWith("3")) {
        commit3 = new GitCommit(rev);
      } else if (rev.getFullMessage().endsWith("2")) {
        commit2 = new GitCommit(rev);
      } else if (rev.getFullMessage().endsWith("1")) {
        commit1 = new GitCommit(rev);
      }
    }
    textFile = new File(repo.getPath() + "/textFile.txt");
    GitFile file = new GitFile(textFile.getTotalSpace(), textFile);
    assertEquals(textFile.getAbsolutePath(), file.getPath().getAbsolutePath());
    String diff12 = commit1.getDiff(commit2, file);
    String diff13 = commit1.getDiff(commit3, file);
    String diff14 = commit1.getDiff(commit4, file);
    String diff32 = commit3.getDiff(commit2, file);
    String diff42 = commit4.getDiff(commit2, file);


    assertTrue(diff32.contains("Neuer Inhalt des Files"));
    assertFalse(diff42.contains("data 2"));


    assertEquals(0, commit4.getDiff(commit3, file).length());
    assertTrue(commit3.getDiff(commit2, file).contains("Neuer Inhalt des Files"));
  }

  @Test
  void getterTests() throws GitAPIException {
    Iterable<RevCommit> log = git.log().call();
    RevCommit jGitCommit = log.iterator().next();
    GitCommit gitCommit = new GitCommit(jGitCommit);
    assertEquals(jGitCommit.getName(), gitCommit.getHash());
    assertEquals(jGitCommit, gitCommit.getRevCommit());
    assertEquals(jGitCommit.getParent(0), gitCommit.getParents()[0].getRevCommit());
    assertEquals(((long)jGitCommit.getCommitTime())*1000, gitCommit.getDate().getTime());
    assertTrue(jGitCommit.abbreviate(7).name().equals(gitCommit.getHashAbbrev()));
    assertEquals(jGitCommit.getShortMessage(), gitCommit.getShortMessage());
    assertEquals(jGitCommit.getFullMessage(), gitCommit.getMessage());
    assertEquals(jGitCommit.getCommitterIdent().getName(),
        gitCommit.getAuthor().getName());
    assertEquals(jGitCommit.getCommitterIdent().getEmailAddress(),
        gitCommit.getAuthor().getEmail());

  }


}
