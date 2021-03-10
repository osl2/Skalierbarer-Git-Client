package commands;

import controller.GUIController;
import git.AbstractGitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;

import static org.mockito.Mockito.mockStatic;

public abstract class AbstractCommandTest extends AbstractGitTest {
    protected static GUIControllerTestable guiControllerTestable;
    protected static MockedStatic<GUIController> mockedController;

    @BeforeEach
    protected void setup() {
        guiControllerTestable = new GUIControllerTestable();
        mockedController = mockStatic(GUIController.class);
        mockedController.when(GUIController::getInstance).thenReturn(guiControllerTestable);
        guiControllerTestable.resetTestStatus();
    }

    @AfterEach
    protected void tearDownCommandTest() {
        mockedController.close();
    }


}
