package git;

import commands.Remote;
import git.exception.GitException;
import org.apache.commons.io.FileSystem;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import settings.Settings;
import shaded.org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GitFacadeTest extends AbstractGitTest {

  File fileNotStaged;
  @SuppressWarnings("unused")
  @TempDir
  File newRepo;
  GitFacade facade;


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

  @BeforeEach
  public void setGitFacade() {
    facade = new GitFacade();
  }

  @Test
  void stashTest() {
    //Currently not implemented
    assertThrows(AssertionError.class, () -> facade.createStash());
  }

  @Test
  void checkoutTest() throws GitAPIException, IOException, GitException {
    git.checkout().setName("NeuerBranch").setCreateBranch(true).call();

    GitBranch branch = new GitBranch("");
    for (Ref branchGit : git.branchList().call()) {
      if (branchGit.getName().endsWith("master")) {
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
    facade.setRepositoryPath(newRepo);
    assertEquals(newRepo, Settings.getInstance().getActiveRepositoryPath());
    assertEquals(GitData.getRepository().getDirectory(), new File(newRepo, ".git"));
    facade.setRepositoryPath(repo);
    assertEquals(repo, Settings.getInstance().getActiveRepositoryPath());
    assertEquals(git.getRepository().getDirectory(), Git.open(repo).getRepository().getDirectory());
  }

  @Test
  public void pullOperationTest() {
    //not implemented
    assertThrows(AssertionError.class, () -> facade.createStash());
  }

  @Test
  public void remoteAddOperationTest() throws GitException, GitAPIException {
    facade.remoteAddOperation("Name", "URL");
    assertEquals(1, git.remoteList().call().size());
    assertEquals("Name", git.remoteList().call().iterator().next().getName());
    assertEquals("URL", git.remoteList().call().iterator().next().getURIs().iterator().next().toString());
  }

  @Test
  public void cloneRepositoryTest() throws GitException, IOException {
    File destination = new File(repo, "newFolder");
    FileUtils.forceMkdir(destination);
    assertThrows(GitException.class, () -> facade.cloneRepository("https://git.scc.kit.edu/pse-git-client/entwurf.git", destination, true), "");

    FileUtils.forceDelete(destination);
    FileUtils.forceMkdir(destination);

    //Cloning a test-Repo from Git-Hub
    facade.cloneRepository("https://github.com/rmccue/test-repository.git", destination, false);
    File[] filesCloned = destination.listFiles();
    assertTrue(filesCloned.length > 0);
    boolean containsGit = false;
    boolean containsOpml = false;
    boolean containsReadme = false;
    boolean containsOther = false;
    for (File file : filesCloned) {
      if (file.getName().equals(".git")) {
        containsGit = true;
      } else if (file.getName().equals("opml.php")) {
        containsOpml = true;
      } else if (file.getName().equals("README")) {
        containsReadme = true;
      } else {
        containsOther = true;
      }
    }
    assertTrue(containsGit);
    assertTrue(containsOpml);
    assertTrue(containsReadme);
    assertFalse(containsOther);
  }


  @Test
  public void fetchRemotesTest() throws GitAPIException, GitException, IOException {
    String gitUrl = "https://github.com/rmccue/test-repository.git";

    //Get all Branches of Remote repo and instantiate a master Branch
    GitBranch masterBranch = new GitBranch("");
    Collection<Ref> refs = Git.lsRemoteRepository()
        .setHeads(true)
        .setRemote(gitUrl)
        .call();
    for(Ref ref : refs){
      if (ref.getName().contains("master")){
        masterBranch = new GitBranch(ref);
      }
    }

    //Cloning Repository
    deleteDir(repo);  //make an empty directory
    git = Git.cloneRepository()
        .setURI(gitUrl)
        .setDirectory(repo)
        .setCloneAllBranches(true)
        .setTransportConfigCallback(CredentialProviderHolder::configureTransport)
        .setCloneSubmodules(true)
        .call();

    GitRemote remote = new GitRemote(gitUrl, "user", "origin");
    ArrayList <GitRemote> remotesToFetch = new ArrayList<>();
    remotesToFetch.add(remote);
    assertEquals(1, remotesToFetch.size());
    facade.fetchRemotes(remotesToFetch);

    remote.addBranch(masterBranch);
    remotesToFetch = new ArrayList<>();
    remotesToFetch.add(remote);
    assertEquals(1, remotesToFetch.size());
    facade.fetchRemotes(remotesToFetch);

    remotesToFetch = new ArrayList<>();
    remotesToFetch.add(new GitRemote("blabla", "user", "name!"));
    ArrayList<GitRemote> finalRemotesToFetch = remotesToFetch;
    assertThrows(GitException.class, () -> facade.fetchRemotes(finalRemotesToFetch));

  }


}
