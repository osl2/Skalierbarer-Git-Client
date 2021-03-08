package views;

import commands.AbstractCommandTest;
import dialogviews.FindComponents;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import util.JOptionPaneTestable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;

class AddCommitViewTest extends AbstractCommandTest {
    private FindComponents find;
    private AddCommitView addCommitView;
    private JPanel panel;
    private JList modifiedChangedFilesList;
    private JList newFilesList;
    private JList deletedFilesList;
    private JTextArea commitMessageTextArea;
    private JButton commitButton;
    private File file;
    private static MockedStatic<JOptionPane> mockedJOptionPane;
    private static JOptionPaneTestable jOptionPaneTestable;

    @BeforeAll
    private static void setupMockedJOptionPane() {
        jOptionPaneTestable = new JOptionPaneTestable();
        mockedJOptionPane = mockStatic(JOptionPane.class);
        mockedJOptionPane.when(() -> JOptionPane.showConfirmDialog(any(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(JOptionPaneTestable.showConfirmDialog(null, "Message", "Title", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE));
    }

    @AfterAll
    private static void closeMockedJOptionPane() {
        jOptionPaneTestable.resetTestStatus();
    }

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
        createNewFile();
        //reload view
        prepare();

        //newFilesList should now contain one element
        assertEquals(1, newFilesList.getModel().getSize());

        mouseListenerTest(newFilesList);
    }

    @Test
    void modifiedChangedFilesListTest() throws IOException, GitAPIException {
        createNewFile();
        add();
        commit();
        modifyFile();

        //reload view
        prepare();

        assertEquals(1, modifiedChangedFilesList.getModel().getSize());

        mouseListenerTest(modifiedChangedFilesList);
    }


    @Test
    void commitButtonEmptyStagingAreaTest() {
        fireCommitButton();
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    @Test
    void commitButtonEmptyMessageTest() throws IOException, GitAPIException {
        createNewFile();
        add();
        fireCommitButton();
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    @Test
    void commitButtonTest() throws IOException, GitAPIException {
        createNewFile();
        add();
        assertTrue(git.status().call().getAdded().contains(file.getName()));
        //reload the view
        prepare();

        commitMessageTextArea.setText("Test commit");
        fireCommitButton();
        assertTrue(jOptionPaneTestable.isShowConfirmDialogCalled());
        //since file should be pre-selected, commit should execute successfully and default view should be restored
        assertTrue(guiControllerTestable.restoreDefaultViewCalled);
    }

    private void mouseListenerTest(JList list) {
        Point pointClicked = list.getLocation();
        MouseEvent mouseEvent = new MouseEvent(list, MouseEvent.MOUSE_CLICKED, 100, 0, pointClicked.x,
                pointClicked.y, 1, false, MouseEvent.BUTTON1);
        for (MouseListener listener : list.getMouseListeners()) {
            listener.mouseClicked(mouseEvent);
        }
        AddCommitView.FileListItem item = (AddCommitView.FileListItem) list.getModel().getElementAt(0);
        //item should now be selected
        assertTrue(item.isSelected());

    }

    private void fireCommitButton() {
        commitButton = (JButton) find.getChildByName(panel, "commitButton");
        assertNotNull(commitButton);
        for (ActionListener listener : commitButton.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(commitButton, ActionEvent.ACTION_PERFORMED, "Commit button clicked"));
        }
    }

    private void createNewFile() throws IOException {
        //create a new file so that newFilesList is not empty
        file = new File(repo, "file");
        new FileOutputStream(file).close();
    }

    private void add() throws GitAPIException {
        git.add().addFilepattern(repo.toPath().relativize(file.toPath()).toString()).call();
    }

    private void commit() throws GitAPIException {
        git.commit().setCommitter("Tester", "tester@example.com").setMessage("Test commit").call();
        assertTrue(git.status().call().isClean());
    }

    private void modifyFile() throws IOException {
        FileWriter out = new FileWriter(file);
        out.write("Test");
        out.close();
    }
}
