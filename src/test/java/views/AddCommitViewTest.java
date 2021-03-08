package views;

import commands.AbstractCommandTest;
import dialogviews.FindComponents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AddCommitViewTest extends AbstractCommandTest {
    private FindComponents find;
    private AddCommitView addCommitView;
    private JPanel panel;
    private JList modifiedChangedFilesList;
    private JList newFilesList;
    private JList deletedFilesList;
    private JTextArea commitMessageTextArea;

    @BeforeEach
    void prepare() {
        find = new FindComponents();
        addCommitView = new AddCommitView();
        panel = addCommitView.getView();
        modifiedChangedFilesList = (JList) find.getChildByName(panel, "modifiedChangedFilesList");
        newFilesList = (JList) find.getChildByName(panel, "newFilesList");
        deletedFilesList = (JList) find.getChildByName(panel, "deletedFilesList");
        commitMessageTextArea = (JTextArea) find.getChildByName(panel, "commitMessageTextArea");

    }

    @Test
    void loadAddCommitView() {
        assertNotNull(modifiedChangedFilesList);
        assertNotNull(newFilesList);
        assertNotNull(deletedFilesList);

        assertNotNull(commitMessageTextArea);
        assertEquals(0, commitMessageTextArea.getText().compareTo(AddCommitView.DEFAULT_COMMIT_MESSAGE));
    }

    @Test
    void loadAddCommitViewWithText() {
        addCommitView = new AddCommitView("Test");
        panel = addCommitView.getView();
        modifiedChangedFilesList = (JList) find.getChildByName(panel, "modifiedChangedFilesList");
        newFilesList = (JList) find.getChildByName(panel, "newFilesList");
        deletedFilesList = (JList) find.getChildByName(panel, "deletedFilesList");
        commitMessageTextArea = (JTextArea) find.getChildByName(panel, "commitMessageTextArea");
        assertEquals(0, commitMessageTextArea.getText().compareTo("Test"));
    }

    @Test
    void cancelButtonTest() {
        JButton cancelButton = (JButton) find.getChildByName(panel, "cancelButton");
        assertNotNull(cancelButton);
        for (ActionListener listener : cancelButton.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(cancelButton, ActionEvent.ACTION_PERFORMED, "Cancel button clicked"));
        }
        assertTrue(guiControllerTestable.restoreDefaultViewCalled);

    }

    @Test
    void commitMessageTextAreaEdited() {
        assertNotNull(commitMessageTextArea);
        assertEquals(0, commitMessageTextArea.getText().compareTo(AddCommitView.DEFAULT_COMMIT_MESSAGE));

        //when the focus is gained, the default commit message should disappear
        for (FocusListener listener : commitMessageTextArea.getFocusListeners()) {
            listener.focusGained(new FocusEvent(commitMessageTextArea, FocusEvent.FOCUS_GAINED, false));
        }
        assertEquals(0, commitMessageTextArea.getText().compareTo(""));

        //when the focus is lost, the default commit message should reappear in case the user has not entered anything
        for (FocusListener listener : commitMessageTextArea.getFocusListeners()) {
            listener.focusLost(new FocusEvent(commitMessageTextArea, FocusEvent.FOCUS_LOST, false));
        }
        assertEquals(0, commitMessageTextArea.getText().compareTo(AddCommitView.DEFAULT_COMMIT_MESSAGE));

        //enter a new text
        String newCommitMessage = "This text should not be deleted";
        commitMessageTextArea.setText(newCommitMessage);

        //when the focus is gained, the new commit message should not be changed
        for (FocusListener listener : commitMessageTextArea.getFocusListeners()) {
            listener.focusGained(new FocusEvent(commitMessageTextArea, FocusEvent.FOCUS_GAINED, false));
        }
        assertEquals(0, commitMessageTextArea.getText().compareTo(newCommitMessage));

        //when the focus is lost, the new commit message should not be changed
        for (FocusListener listener : commitMessageTextArea.getFocusListeners()) {
            listener.focusLost(new FocusEvent(commitMessageTextArea, FocusEvent.FOCUS_LOST, false));
        }
        assertEquals(0, commitMessageTextArea.getText().compareTo(newCommitMessage));
    }

    @Test
    void newFilesListTest() throws IOException {
        //create a new file so that newFilesList is not empty
        File file = new File(repo, "file");
        new FileOutputStream(file).close();

        prepare();

        //newFilesList should now contain one element
        assertEquals(1, newFilesList.getModel().getSize());

        Point pointClicked = newFilesList.getLocation();
        MouseEvent mouseEvent = new MouseEvent(newFilesList, MouseEvent.MOUSE_CLICKED, 100, 0, pointClicked.x,
                pointClicked.y, 1, false, MouseEvent.BUTTON1);

        for (MouseListener listener : newFilesList.getMouseListeners()) {
            listener.mouseClicked(mouseEvent);
        }
        AddCommitView.FileListItem item = (AddCommitView.FileListItem) newFilesList.getModel().getElementAt(0);
        //item should now be selected
        assertTrue(item.isSelected());
    }
}
