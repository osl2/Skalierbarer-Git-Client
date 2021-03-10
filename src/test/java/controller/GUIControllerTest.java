package controller;

import dialogviews.IDialogView;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import settings.Data;
import settings.Settings;
import views.IView;
import views.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GUIControllerTest {
    @TempDir
    static File repo;
    MockedStatic<JOptionPane> mockJOptionPane;
    MockedConstruction<JDialog> mockedJDialog;
    MockedConstruction<MainWindow> mockedMainWindow;
    GUIController guiController;
    Git git;

    @BeforeAll
    static void beforeAll() throws GitAPIException {
        Git.init().setDirectory(repo).setBare(false).call().close();
    }

    @BeforeEach
    void setUp() throws IOException {
        // do not open any dialogs
        mockJOptionPane = mockStatic(JOptionPane.class);
        mockedJDialog = mockConstruction(JDialog.class, (mock, context) -> {
            doNothing().when(mock).setVisible(anyBoolean());
        });
        mockedMainWindow = mockConstruction(MainWindow.class, (mock, context) -> {
            doThrow(new AssertionError("Set CommandLine null")).when(mock).setCommandLineText(null);
        });

        Settings s = Settings.getInstance();
        s.setLevel(Data.getInstance().getLevels().get(0));
        s.setActiveRepositoryPath(repo);
        guiController = GUIController.getInstance();
        guiController.initializeMainWindow();
        git = Git.open(repo);
    }

    @AfterEach
    void tearDown() {
        mockJOptionPane.close();
        mockedJDialog.close();
        mockedMainWindow.close();
        git.close();
    }

    @Test
    void errorHandlerHugeMessageTest() {
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            msg.append("Line ");
            msg.append(i);
            msg.append(System.lineSeparator());
        }

        guiController.errorHandler(msg.toString());
        mockJOptionPane.verify(() -> JOptionPane.showMessageDialog(
                any(Component.class),
                any(JPanel.class),
                anyString(),
                anyInt()
        ));

    }

    @Test
    void updateUpdatesDialog() {
        IDialogView mockDialog = mock(IDialogView.class);

        when(mockDialog.getPanel()).thenReturn(new JPanel());
        when(mockDialog.getDimension()).thenReturn(new Dimension(1, 1));
        when(mockDialog.getTitle()).thenReturn("Title");
        guiController.openDialog(mockDialog);
        verify(mockedJDialog.constructed().get(0)).setVisible(anyBoolean());

        guiController.update();
        verify(mockDialog).update();

        guiController.closeDialogView();

    }


    @Test
    void setCommandLineTest() {
        String empty = "";
        String test = "TEST";
        guiController.setCommandLine(empty);
        verify(mockedMainWindow.constructed().get(0)).setCommandLineText(eq(empty));
        guiController.setCommandLine(test);
        verify(mockedMainWindow.constructed().get(0)).setCommandLineText(eq(test));
        guiController.setCommandLine(null);
    }

    @Test
    void restoreDefaultViewWorks() {
        guiController.restoreDefaultView();
        verify(mockedMainWindow.constructed().get(0)).setView(any(IView.class));

    }

}
