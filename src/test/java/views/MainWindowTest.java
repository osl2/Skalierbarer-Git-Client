package views;

import dialogviews.FindComponents;
import git.AbstractGitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MainWindowTest extends AbstractGitTest {
    private MainWindow mainWindow;
    private JTextField commandLineTextField;
    private JPanel buttonPanel;
    private JLabel branchLabel;
    private JPanel viewPanel;

    @BeforeEach
    void prepare() {
        mainWindow = new MainWindow();
        mainWindow.open();
        JPanel contentPane = (JPanel) mainWindow.getContentPane();
        commandLineTextField = (JTextField) FindComponents.getChildByName(contentPane, "commandLineTextField");
        buttonPanel = (JPanel) FindComponents.getChildByName(contentPane, "buttonPanel");
        branchLabel = (JLabel) FindComponents.getChildByName(contentPane, "branchLabel");
        viewPanel = (JPanel) FindComponents.getChildByName(contentPane, "viewPanel");
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
        mainWindow.clearButtonPanel();
        assertEquals(0, buttonPanel.getComponents().length);
    }

    @Test
    void addButtonTest() {
        boolean actionListenerCalled;
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
    void openTest() {
        //TODO: how to test without heads?
        mainWindow.open();
        assertEquals(WindowConstants.EXIT_ON_CLOSE, mainWindow.getDefaultCloseOperation());
        assertNotNull(mainWindow.getContentPane());
        assertEquals(1280, mainWindow.getPreferredSize().width);
        assertEquals(720, mainWindow.getPreferredSize().height);
        assertTrue(mainWindow.isVisible());
    }

    @Test
    void updateTest() {
        //TODO: implement me
    }

    @Test
    void testBranchLabel() throws IOException {
        String currentBranch = repository.getFullBranch();
        assertEquals(0, branchLabel.getText().compareTo(currentBranch));
    }

}
