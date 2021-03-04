package commands;

import controller.GUIController;
import git.AbstractGitTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;

import static org.mockito.Mockito.mockStatic;

public abstract class AbstractCommandTest extends AbstractGitTest {
    static GUIControllerTestable guiControllerTestable;
    static MockedStatic<GUIController> mockedController;

    @BeforeAll
    static void setup() {
        guiControllerTestable = new GUIControllerTestable();
        mockedController = mockStatic(GUIController.class);
        mockedController.when(GUIController::getInstance).thenReturn(guiControllerTestable);
        guiControllerTestable.resetTestStatus();
    }

    @AfterAll
    static void tearDown() {
        mockedController.close();
    }


}
