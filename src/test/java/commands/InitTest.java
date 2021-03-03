package commands;

import controller.GUIController;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class InitTest {
  @TempDir
  protected static File directory;
  static GUIControllerTestable guiControllerTestable;
  static MockedStatic<GUIController> mockedController;
  Init init;

  @BeforeEach
  public void prepareTest() throws IOException {
    guiControllerTestable = new GUIControllerTestable();
    mockedController = mockStatic(GUIController.class);
    mockedController.when(GUIController::getInstance).thenReturn(guiControllerTestable);
    guiControllerTestable.resetTestStatus();
    init = new Init();
    FileUtils.forceMkdir(directory);
    assertTrue(directory.isDirectory());
  }

  @AfterEach
  public void tearDown() throws IOException {
    init = null;
    FileUtils.forceDelete(directory);
    mockedController.close();
  }

  @Test
  void testInitExecute() {
    init.setPathToRepository(directory);
    assertTrue(init.execute());
    File file = new File(directory, ".git");
    assertTrue(file.exists());
    assertEquals("git init", init.getCommandLine());
  }

  @Test
  void testExecuteFail() {
    assertFalse(init.execute());
    assertTrue(guiControllerTestable.errorHandlerMSGCalled);
  }

  @Test
  void testGetter() {
    assertNull(init.getCommandLine());
    assertNotNull(init.getDescription());
    assertNotNull(init.getName());
  }
}
