package git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Before;
import org.junit.Test;
import settings.Settings;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Is testing GitData
 */
public class GitDataTest {

  GitData gitData;
  Git git;
  Repository repository;
  Settings settings;

  @Before
  public void init (){
    settings = Settings.getInstance();
    settings.setActiveRepositoryPath(new File("src/test/resources/Test"));
    gitData = new GitData();
    try {
      git = Git.open(new File("src/test/resources/Test"));
      repository = git.getRepository();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void getBranchesTest() throws GitAPIException {
    List<GitBranch> branchesGitData = gitData.getBranches();
    List<Ref> branchesJGit = git.branchList().call();
    assertEquals(branchesGitData.size(), branchesJGit.size());
    // TODO: mehr testen

  }

}

