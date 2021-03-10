package commands;

import git.GitCommit;
import git.exception.GitException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class RevertTest extends AbstractCommandTest {

  @Test
  void testExecute() throws IOException, GitException {
    GitCommit commit = gitData.getCommits().next();
    Revert revert = new Revert();
    revert.setChosenCommit(commit);
    assertEquals(commit.getMessage(), revert.getChosenCommit().getMessage());
    assertTrue(revert.execute());
    assertNotNull(revert.getCommandLine());
  }

  @Test
  void testMetaData() {
    Revert revert = new Revert();
    assertNull(revert.getCommandLine());
    assertNotNull(revert.getName());
    assertNotNull(revert.getDescription());
    revert.onButtonClicked();
    assertTrue(guiControllerTestable.openDialogCalled);
  }

  @Test
  void testFalseInput() {
    Revert revert = new Revert();
    assertFalse(revert.execute());
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
  }
}
