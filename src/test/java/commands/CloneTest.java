package commands;

import git.exception.GitException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CloneTest extends AbstractCommandTest {

  protected static File remoteDir = new File("src" + System.getProperty("file.separator") + "test" +
          System.getProperty("file.separator") + "resources" + System.getProperty("file.separator") + "Test" +
          System.getProperty("file.separator") + "remote");

  @BeforeEach
  protected void createRemoteDir() throws IOException {
    deleteDir(remoteDir);
    FileUtils.forceMkdir(remoteDir);
  }

  @AfterEach
  protected void cleanUp() {
    assertTrue(deleteDir(remoteDir.getAbsoluteFile()));
  }

  @Test
  void testCloneExecute() {
    Clone clone = new Clone();
    clone.cloneRecursive(true);
    clone.setDestination(remoteDir);
    clone.setGitURL(repo.getPath());
    assertTrue(clone.execute());
    File[] files = remoteDir.listFiles();
    assertEquals(2, files.length);
    File gitFile = null;
    for (int i = 0; i < files.length; i++) {
      if (files[i].getName().compareTo(".git") == 0) {
        gitFile = files[i];
      }
    }
    assertNotNull(gitFile);
    assertNotNull(clone.getCommandLine());
  }

  @Test
  void testMetaData() {
    Clone clone = new Clone();
    assertNotNull(clone.getName());
    assertNotNull(clone.getDescription());
    assertNull(clone.getCommandLine());
    clone.onButtonClicked();
    assertTrue(guiControllerTestable.openDialogCalled);
  }

  @Test
  void testExceptionHandling() throws GitException {
    Clone clone = new Clone();
    assertFalse(clone.execute());
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
  }
}
