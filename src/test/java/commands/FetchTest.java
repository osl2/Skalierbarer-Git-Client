package commands;

import git.GitData;
import git.GitRemote;
import git.exception.GitException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class FetchTest extends AbstractRemoteTest {

  @Test
  void testFetchExecuteRemote() {
    Fetch fetch = new Fetch();
    fetch.addRemote(gitData.getRemotes().get(0));
    assertTrue(fetch.execute());
    assertFalse(guiControllerTestable.errorHandlerMSGCalled | guiControllerTestable.errorHandlerECalled);
    assertTrue(fetch.getCommandLine().contains("git fetch"));
  }

  @Test
  void testFetchExecuteRemoteBranch() throws GitException {
    Fetch fetch = new Fetch();
    fetch.addBranch(gitData.getRemotes().get(0), gitData.getBranches(gitData.getRemotes().get(0)).get(0));
    assertTrue(fetch.execute());
    assertFalse(guiControllerTestable.errorHandlerMSGCalled | guiControllerTestable.errorHandlerECalled);
    assertTrue(fetch.getCommandLine().contains("git fetch"));
  }

  @Test
  void testMetaData() {
    Fetch fetch = new Fetch();
    assertNotNull(fetch.getDescription());
    assertNotNull(fetch.getName());
    assertNull(fetch.getCommandLine());
    fetch.onButtonClicked();
    assertTrue(guiControllerTestable.openDialogCalled);
  }

  @Test
  void testDialogViewDoesNotOpen() {
    MockedConstruction<GitData> mockConst = mockConstruction(GitData.class, (mock, context) -> {
      when(mock.getBranches(any(GitRemote.class))).thenReturn(new ArrayList<>());
    });
    Fetch fetch = new Fetch();
    fetch.onButtonClicked();
    assertFalse(guiControllerTestable.openDialogCalled);
    mockConst.close();
  }
}
