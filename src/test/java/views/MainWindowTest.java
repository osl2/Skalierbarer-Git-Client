package views;

import commands.AbstractCommandTest;
import dialogviews.FindComponents;
import levels.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockConstruction;

class MainWindowTest extends AbstractCommandTest {
    private MainWindow mainWindow;
    private JTextField commandLineTextField;
    private JPanel buttonPanel;
    private JLabel branchLabel;

    @BeforeEach
    void prepare() {
        mainWindow = new MainWindow();
        loadComponents();
    }

    private void loadComponents() {
        JPanel contentPane = mainWindow.getPanel();
        commandLineTextField = (JTextField) FindComponents.getChildByName(contentPane, "commandLineTextField");
        buttonPanel = (JPanel) FindComponents.getChildByName(contentPane, "buttonPanel");
        branchLabel = (JLabel) FindComponents.getChildByName(contentPane, "branchLabel");
    }

    @Test
    void setViewTest() {
        AddCommitView addCommitView = new AddCommitView();
        mainWindow.setView(addCommitView);
        assertEquals(addCommitView, mainWindow.getView());
    }

    @Test
    void setCommandLineTest() {
        String commandLineText = "Test command line";
        mainWindow.setCommandLineText(commandLineText);
        assertEquals(0, commandLineTextField.getText().compareTo(commandLineText));
    }

    @Test
    void clearButtonPanelTest() {
        mainWindow.addButton("newButton", "newButtonToolTip", e -> {
            //do nothing
        });
        assertEquals(1, buttonPanel.getComponents().length);
        mainWindow.clearButtonPanel();
        assertEquals(0, buttonPanel.getComponents().length);
    }

    @Test
    void addButtonTest() {
        String newButtonText = "New Button";
        String newButtonToolTipText = "Tooltip for new button";
        ActionListener newButtonListener = e -> {
            //color button in red
            JButton source = (JButton) e.getSource();
            source.setBackground(Color.RED);
        };

        mainWindow.addButton(newButtonText, newButtonToolTipText, newButtonListener);

        //button panel should not be empty
        Component[] buttonPanelComponents = buttonPanel.getComponents();
        assertNotEquals(0, buttonPanelComponents.length);
        JButton buttonFromPanel = (JButton) buttonPanelComponents[0];

        assertEquals(newButtonText, buttonFromPanel.getText());
        assertEquals(newButtonToolTipText, buttonFromPanel.getToolTipText());
        ActionListener[] listeners = buttonFromPanel.getActionListeners();
        assertEquals(1, listeners.length);

        listeners[0].actionPerformed(new ActionEvent(buttonFromPanel, ActionEvent.ACTION_PERFORMED, "Button clicked"));
        assertEquals(Color.RED, buttonFromPanel.getBackground());
    }

    @Test
    void updateTest() throws IOException {
        mainWindow.setView(new AddCommitView());
        assertNotNull(mainWindow.getView());
        mainWindow.update();
        loadComponents();
        String currentBranch = repository.getFullBranch();
        assertEquals(0, branchLabel.getText().compareTo(currentBranch));
        assertNotNull(mainWindow.getJMenuBar());
    }

    @Test
    void menuBarTest() {
        //mock fileChooser to return an invalid git directory path
        MockedConstruction<JFileChooser> mockedJFileChooser = mockConstruction(JFileChooser.class, (mock, context) -> {
            doReturn(JFileChooser.APPROVE_OPTION).when(mock).showOpenDialog(any());
            doReturn(new File(repo + System.lineSeparator() + "testFile.txt")).when(mock).getSelectedFile();
        });

        //view must be updated in order to create the menubar
        mainWindow.update();
        JMenuBar menuBar = mainWindow.getJMenuBar();
        int numMenus = menuBar.getMenuCount();
        assertEquals(1, numMenus);
        JMenu menu = menuBar.getMenu(0);
        int numSubMenus = menu.getMenuComponentCount();
        assertEquals(3, numSubMenus);
        JMenu recentlyUsed = (JMenu) FindComponents.getChildByName(menu, "recentlyUsed");
        JMenuItem openItem = (JMenuItem) FindComponents.getChildByName(menu, "openItem");
        JMenuItem settingsItem = (JMenuItem) FindComponents.getChildByName(menu, "settingsItem");
        assertNotNull(recentlyUsed);
        assertNotNull(openItem);
        assertNotNull(settingsItem);

        int numRepos = recentlyUsed.getMenuComponentCount();
        assertTrue(numRepos > 0);
        JMenuItem firstRepo = (JMenuItem) recentlyUsed.getMenuComponent(0);
        String path = firstRepo.getText();
        for (ActionListener listener : firstRepo.getListeners(ActionListener.class)) {
            listener.actionPerformed(
                    new ActionEvent(recentlyUsed, ActionEvent.ACTION_PERFORMED, "Button clicked"));
        }
        assertEquals(0, Settings.getInstance().getActiveRepositoryPath().getPath().compareTo(path));
        assertTrue(guiControllerTestable.updateCalled);

        for (ActionListener listener : openItem.getListeners(ActionListener.class)) {
            listener.actionPerformed(
                    new ActionEvent(openItem, ActionEvent.ACTION_PERFORMED, "Button clicked"));
        }
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);

        //set dummy level for instantiation of SettingsDialogView
        Settings.getInstance().setLevel(new Level("Level", new LinkedList<>(), 1));
        for (ActionListener listener : settingsItem.getListeners(ActionListener.class)) {
            listener.actionPerformed(
                    new ActionEvent(openItem, ActionEvent.ACTION_PERFORMED, "Button clicked"));
        }
        assertTrue(guiControllerTestable.openDialogCalled);

        mockedJFileChooser.close();

    }

}
