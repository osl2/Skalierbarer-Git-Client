package commands;

import git.GitData;
import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractRemoteTest extends AbstractCommandTest {

  protected static File remoteDir = new File("src" + System.getProperty("file.separator") + "test" +
          System.getProperty("file.separator") + "resources" + System.getProperty("file.separator") + "Test" +
          System.getProperty("file.separator") + "remote");

  @BeforeEach
  protected void createRemoteDir() throws IOException, GitAPIException, GitException {
    deleteDir(remoteDir);
    FileUtils.forceMkdir(remoteDir);
    Git.cloneRepository().setURI(repo.getPath() + System.getProperty("file.separator") + ".git").setDirectory(remoteDir).setCloneAllBranches(true).call();
    settings.setActiveRepositoryPath(remoteDir);
    gitData = new GitData();
    gitData.reinitialize();
    assertEquals(1, gitData.getBranches().size());
  }

}
