package git;

import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import settings.Settings;
import shaded.org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class GitFacadeTest extends AbstractGitTest {

  File fileNotStaged;
  @TempDir
  File newRepo;


  @Override
  public void init() throws IOException, GitAPIException, GitException, URISyntaxException {
    super.init();
    settings.setUser(new GitAuthor("Author", "authoremail@example.com"));
    File fileNotStaged = new File(repo.getPath() + "/textFile3.txt");
    FileWriter fr = new FileWriter(fileNotStaged, true);
    fr.write("Neuer Inhalt des Files");
    fr.close();
    this.fileNotStaged = fileNotStaged;

    git.close();
  }

  @Test
  void stashTest(){
    GitFacade facade = new GitFacade();
    //Currently not implemented
    assertThrows(AssertionError.class, () -> facade.createStash());
  }

  @Test
  void checkoutTest() throws GitAPIException, IOException, GitException {
    git.checkout().setName("NeuerBranch").setCreateBranch(true).call();

    GitFacade facade = new GitFacade();
    GitBranch branch = new GitBranch("");
    for (Ref branchGit: git.branchList().call()){
      if (branchGit.getName().endsWith("master")){
        branch = new GitBranch(branchGit);
      }
    }
    String currentBranch = repository.getBranch();
    assertEquals("NeuerBranch", currentBranch);
    facade.checkout(branch);
    currentBranch = repository.getBranch();
    assertEquals("master", currentBranch);
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

  @Test
  public void setReopsitoryPathTest() throws IOException {
    FileUtils.forceMkdir(newRepo);
    GitFacade facade = new GitFacade();
    facade.setRepositoryPath(newRepo);
    assertEquals(newRepo, Settings.getInstance().getActiveRepositoryPath());
    assertEquals(GitData.getRepository().getDirectory(), new File (newRepo, ".git"));
    facade.setRepositoryPath(repo);
    assertEquals(repo, Settings.getInstance().getActiveRepositoryPath());
    assertEquals(git.getRepository().getDirectory(), Git.open(repo).getRepository().getDirectory());
  }
}
