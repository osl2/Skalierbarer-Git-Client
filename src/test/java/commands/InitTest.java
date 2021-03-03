package commands;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InitTest {
  @TempDir
  protected static File directory;

  @BeforeEach
  public void prepareTest() throws IOException {
    FileUtils.forceMkdir(directory);
    assertTrue(directory.isDirectory());
  }

  @AfterEach
  public void tearDown() throws IOException {
    FileUtils.forceDelete(directory);
  }

  @Test
  public void testInitExecute() {
    Init init = new Init();
    init.setPathToRepository(directory);
    assertTrue(init.execute());
    File file = new File(directory, ".git");
    assertTrue(file.exists());
    assertEquals(init.getCommandLine(), "git init");
  }


}
