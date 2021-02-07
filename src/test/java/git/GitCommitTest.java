package git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitCommitTest extends AbstractGitTest {

  @Override
  protected void setupRepo() throws GitAPIException, IOException {
    Git.init().setDirectory(repo).setBare(false).call();
    Git git = Git.open(repo);
    git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
            .setMessage("Commit 1").call();
    File textFile = new File(repo.getPath() + "/textFile.txt");
    FileWriter fr = new FileWriter(textFile, true);
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
  public void getChangedFilesTest() throws IOException {
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


}
