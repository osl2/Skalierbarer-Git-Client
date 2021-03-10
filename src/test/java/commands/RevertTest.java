package commands;

import git.GitCommit;
import git.GitData;
import git.exception.GitException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

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
  void openFails() {
    MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) -> {
      when(mock.getBranches()).thenReturn(new ArrayList<>());
    });
    Revert revert = new Revert();
    revert.onButtonClicked();
    assertFalse(guiControllerTestable.openDialogCalled);
    gitDataMockedConstruction.close();
  }

  @Test
  void openFailsBecauseException() {
    MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) -> {
      when(mock.getBranches()).thenThrow(new GitException(""));
    });
    Revert revert = new Revert();
    revert.onButtonClicked();
    assertTrue(guiControllerTestable.errorHandlerECalled);
    gitDataMockedConstruction.close();
  }

  @Test
  void testExceptionHandling() throws IOException {
    MockedConstruction<GitCommit> gitCommitMockedConstruction = mockConstruction(GitCommit.class, (mock, context) -> {
      when(mock.revert()).thenThrow(new GitException(""));
    });
    GitData data = new GitData();
    GitCommit commit = data.getSelectedBranch().getCommit();
    Revert revert = new Revert();
    revert.setChosenCommit(commit);
    assertFalse(revert.execute());
    assertTrue(guiControllerTestable.errorHandlerECalled);
    gitCommitMockedConstruction.close();
  }

  @Test
  void testFalseInput() {
    Revert revert = new Revert();
    assertFalse(revert.execute());
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
  }
}
