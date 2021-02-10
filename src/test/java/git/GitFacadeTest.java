package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;
import shaded.org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitFacadeTest extends AbstractGitTest {

  File fileNotStaged;

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
    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 3", "tester3@example.com").setSign(false)
            .setMessage("Commit 3").call();
    git.commit().setCommitter("Tester 1", "tester1@example.com").setSign(false)
            .setMessage("Commit 4").call();
    File fileNotStaged = new File(repo.getPath() + "/textFile3.txt");
    fr.write("Neuer Inhalt des Files");
    fr.close();
    this.fileNotStaged = fileNotStaged;

    git.close();
  }

  @Override
  public void init() throws IOException, GitAPIException, GitException {
    super.init();
    settings.setUser(new GitAuthor("Author", "authoremail@example.com"));
  }

  @Test
  public void commitCommandTest() throws GitAPIException, GitException {
    GitFacade facade = new GitFacade();
    git.add().addFilepattern(fileNotStaged.getName()).call();
    String commitMessage = RandomStringUtils.random(21);
    facade.commitOperation(commitMessage, false);

    RevCommit commit = git.log().setMaxCount(1).call().iterator().next();
    assertEquals(commitMessage, commit.getFullMessage()); //expected commit-Message
    assertEquals(settings.getUser().getName(), commit.getAuthorIdent().getName()); //expected Author name
    assertEquals(settings.getUser().getEmail(), commit.getAuthorIdent().getEmailAddress()); //expected Author mail
  }
}
