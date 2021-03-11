package git;

import git.exception.*;
import org.eclipse.jgit.api.errors.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

public class GitRemoteTest extends AbstractGitTest {

  GitRemote gitRemote;

  @Override
  public void init() throws URISyntaxException, GitAPIException, GitException, IOException {
    super.init();
    resetRemote();
  }
  private void resetRemote(){
    gitRemote = new GitRemote("url", "user", "name");
  }

  @Test
  void getterAndSetterTest() throws GitException {
    gitRemote.setGitUser("newUser");
    assertEquals(new GitRemote("url", "newUser", "name"), gitRemote);
    resetRemote();
    gitRemote.setName("newName");
    assertEquals(new GitRemote("url", "user", "newName"), gitRemote);
    assertEquals(gitRemote.getName(), "newName");
    resetRemote();
    assertTrue (gitRemote.setUrl("newUrl"));
    assertEquals(new GitRemote("newUrl", "user", "name"), gitRemote);
  }

  @Test
  void equalsTest() {
    assertTrue(gitRemote.equals(gitRemote));
    assertTrue(gitRemote.equals(new GitRemote("url", "user", "name")));
  }
}
