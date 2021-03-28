/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package views;

import commands.AbstractCommandTest;
import dialogviews.FindComponents;
import git.GitFile;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class AddCommitViewTest extends AbstractCommandTest {
    private AddCommitView addCommitView;
    private JPanel panel;
    private JList modifiedChangedFilesList;
    private JList newFilesList;
    private JList deletedFilesList;
    private JTextArea commitMessageTextArea;
    private File file;
    private JCheckBox modifiedChangedFilesCheckBox;
    private JCheckBox newFilesCheckBox;
    private JCheckBox deletedFilesCheckBox;
    private MockedStatic<JOptionPane> mockedJOptionPane;
    private JTextPane diffTextPane;

    @BeforeEach
    void prepare() {
        mockedJOptionPane = Mockito.mockStatic(JOptionPane.class);
        mockedJOptionPane.when(() -> JOptionPane.showConfirmDialog(any(), anyString(), anyString(),
                anyInt(), anyInt()))
                .thenReturn(JOptionPane.YES_OPTION);
        loadComponents();
    }

    @AfterEach
    void closeJOptionPane() {
        mockedJOptionPane.close();
    }

    @BeforeEach
    void loadComponents() {
        addCommitView = new AddCommitView();
        panel = addCommitView.getView();
        modifiedChangedFilesList = (JList) FindComponents.getChildByName(panel, "modifiedChangedFilesList");
        newFilesList = (JList) FindComponents.getChildByName(panel, "newFilesList");
        deletedFilesList = (JList) FindComponents.getChildByName(panel, "deletedFilesList");
        commitMessageTextArea = (JTextArea) FindComponents.getChildByName(panel, "commitMessageTextArea");
        modifiedChangedFilesCheckBox = (JCheckBox) FindComponents.getChildByName(panel, "modifiedChangedFilesCheckBox");
        newFilesCheckBox = (JCheckBox) FindComponents.getChildByName(panel, "newFilesCheckBox");
        deletedFilesCheckBox = (JCheckBox) FindComponents.getChildByName(panel, "deletedFilesCheckBox");
        diffTextPane = (JTextPane) FindComponents.getChildByName(panel, "diffTextPane");

    }

    @Test
    void loadAddCommitViewTest() throws IOException {
        assertNotNull(modifiedChangedFilesList);
        assertNotNull(newFilesList);
        assertNotNull(deletedFilesList);

        assertNotNull(commitMessageTextArea);
        assertEquals(0, commitMessageTextArea.getText().compareTo(repository.readCommitEditMsg()));

    }

    @Test
    void loadAddCommitViewWithText() {
        addCommitView = new AddCommitView("Test");
        panel = addCommitView.getView();
        commitMessageTextArea = (JTextArea) FindComponents.getChildByName(panel, "commitMessageTextArea");
        assertNotNull(commitMessageTextArea);
        assertEquals(0, commitMessageTextArea.getText().compareTo("Test"));
    }

    @Test
    void cancelButtonTest() {
        JButton cancelButton = (JButton) FindComponents.getChildByName(panel, "cancelButton");
        assertNotNull(cancelButton);
        for (ActionListener listener : cancelButton.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(cancelButton, ActionEvent.ACTION_PERFORMED, "Cancel button clicked"));
        }
        assertTrue(guiControllerTestable.restoreDefaultViewCalled);

    }

    @Test
    void commitMessageTextAreaEdited() throws IOException {
        assertNotNull(commitMessageTextArea);
        String commitEdithMsg = repository.readCommitEditMsg();
        assertEquals(0, commitMessageTextArea.getText().compareTo(Objects.requireNonNullElse(commitEdithMsg, AddCommitView.DEFAULT_COMMIT_MESSAGE)));

        //reset the commit message text are
        commitMessageTextArea.setText(AddCommitView.DEFAULT_COMMIT_MESSAGE);

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
        loadComponents();

        //newFilesList should now contain one element
        assertEquals(1, newFilesList.getModel().getSize());

        mouseListenerTest(newFilesList, true);
    }

    @Test
    void modifiedChangedFilesListTest() throws IOException, GitAPIException {
        createNewFile();
        add();
        commit();
        modifyFile();

        //reload view
        loadComponents();

        assertEquals(1, modifiedChangedFilesList.getModel().getSize());

        mouseListenerTest(modifiedChangedFilesList, true);
    }


    @Test
    void commitButtonEmptyStagingAreaTest() {
        JButton commitButton = (JButton) FindComponents.getChildByName(panel, "commitButton");
        fireActionEvent(commitButton);
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    @Test
    void commitButtonEmptyMessageTest() throws IOException, GitAPIException {
        createNewFile();
        add();
        JButton commitButton = (JButton) FindComponents.getChildByName(panel, "commitButton");
        commitMessageTextArea.setText("");
        fireActionEvent(commitButton);
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    /*
    This is global testcase 6 from Pflichtenheft
     */
    @Test
    void globalCommitTest_T6() throws IOException, GitAPIException {
        createNewFile();
        add();
        assertTrue(git.status().call().getAdded().contains(file.getName()));
        //reload the view
        loadComponents();

        JButton commitButton = (JButton) FindComponents.getChildByName(panel, "commitButton");
        commitTest(commitButton);
    }

    @Test
    void amendButtonTest() throws IOException, GitAPIException {
        createNewFile();
        add();
        //reload the view
        loadComponents();

        JButton amendButton = (JButton) FindComponents.getChildByName(panel, "amendButton");
        commitTest(amendButton);
    }

    @Test
    void cancelButtonTestChangesMade() throws IOException, GitAPIException {
        createNewFile();
        loadComponents();

        //status should contain the new file
        assertTrue(git.status().call().getUntracked().contains(file.getName()));
        assertEquals(1, newFilesList.getModel().getSize());
        //get the item
        AddCommitView.FileListItem fileListItem = (AddCommitView.FileListItem) newFilesList.getModel().getElementAt(0);
        //select item to add it to the staging-area
        fileListItem.setSelected(true);

        JButton cancelButton = (JButton) FindComponents.getChildByName(panel, "cancelButton");
        fireActionEvent(cancelButton);

        //status should now be clean, file should have been added
        assertTrue(git.status().call().getAdded().contains(file.getName()));

        assertTrue(guiControllerTestable.restoreDefaultViewCalled);

    }

    private void commitTest(JButton button) throws GitAPIException {
        //staging area should not be empty
        assertFalse(git.status().call().isClean());

        commitMessageTextArea.setText("Test commit");
        fireActionEvent(button);
        //since file should be pre-selected, commit should execute successfully and default view should be restored
        assertTrue(guiControllerTestable.restoreDefaultViewCalled);

        //status should be clean after commit amend has been executed
        assertTrue(git.status().call().isClean());
    }

    @Test
    void updateTest() throws IOException {
        addCommitView.update();
        loadAddCommitViewTest();
    }

    @Test
    void modifiedChangedFilesCheckBoxTest() {
        selectAllCheckBoxTest(modifiedChangedFilesList, modifiedChangedFilesCheckBox);
    }

    @Test
    void newFilesCheckBoxTest() {
        selectAllCheckBoxTest(newFilesList, newFilesCheckBox);
    }

    @Test
    void deletedFilesCheckBoxTest() {
        selectAllCheckBoxTest(deletedFilesList, deletedFilesCheckBox);
    }

    @Test
    void commitMessageTextAreaLoadEarlierMessageTest() throws IOException {
        String oldCommitMessage = "Text from earlier ACV opened";
        commitMessageTextArea.setText(oldCommitMessage);
        git.getRepository().writeCommitEditMsg(oldCommitMessage);
        //reload the view. Commit message should reappear
        loadComponents();
        assertEquals(0, commitMessageTextArea.getText().compareTo(oldCommitMessage));
    }


    void selectAllCheckBoxTest(JList list, JCheckBox checkBox) {
        for (ItemListener listener : checkBox.getItemListeners()) {
            listener.itemStateChanged(new ItemEvent(checkBox, ItemEvent.ITEM_STATE_CHANGED,
                    checkBox, ItemEvent.SELECTED));
        }
        for (int i = 0; i < list.getModel().getSize(); i++) {
            AddCommitView.FileListItem item =
                    (AddCommitView.FileListItem) list.getModel().getElementAt(i);
            assertTrue(item.isSelected());
        }
    }

    private void mouseListenerTest(JList list, boolean expectedState) {
        Point pointClicked = list.getLocation();
        MouseEvent mouseEvent = new MouseEvent(list, MouseEvent.MOUSE_CLICKED, 100, 0, pointClicked.x,
                pointClicked.y, 1, false, MouseEvent.BUTTON1);
        for (MouseListener listener : list.getMouseListeners()) {
            listener.mouseClicked(mouseEvent);
        }
        AddCommitView.FileListItem item = (AddCommitView.FileListItem) list.getModel().getElementAt(0);
        //item should now be selected
        assertEquals(expectedState, item.isSelected());

    }

    private void fireActionEvent(JButton button) {
        assertNotNull(button);
        for (ActionListener listener : button.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, "Button clicked"));
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

    /*
    This is testcase 5 from the Pflichtenheft
     */
    @Test
    void globalAddTest_T5() throws IOException, GitAPIException, GitException {
        createNewFile();
        add();
        commit();
        modifyFile();

        //testcase step: open ACV, cannot be tested here
        //reload view
        loadComponents();
        assertEquals(1, modifiedChangedFilesList.getModel().getSize());

        //Testcase step 2: select an entry in one of the file lists
        mouseListenerTest(modifiedChangedFilesList, true);
        //diff text pane should not be empty
        assertFalse(diffTextPane.getText().isEmpty());

        //Testcase step 3: select file
        modifiedChangedFilesList.setSelectedIndex(0);
        AddCommitView.FileListItem firstItem = (AddCommitView.FileListItem) modifiedChangedFilesList.getSelectedValue();
        assertTrue(firstItem.isSelected());
        GitFile firstFile = firstItem.getGitFile();
        //item should appear in green now, this cannot be tested here

        //Testcase step 4 - 5: click cancel button
        //reset mocked JOptionPane to select NO_OPTION
        mockedJOptionPane.when(() -> JOptionPane.showConfirmDialog(any(), anyString(), anyString(),
                anyInt(), anyInt()))
                .thenReturn(JOptionPane.NO_OPTION);
        cancelButtonTest();
        //file should not have been staged
        assertFalse(firstFile.isStaged());

        //testcase step 6: reopen the view
        //reload the view
        loadComponents();
        assertEquals(1, modifiedChangedFilesList.getModel().getSize());
        firstItem = (AddCommitView.FileListItem) modifiedChangedFilesList.getModel().getElementAt(0);
        assertFalse(firstItem.isSelected());

        //testcase step 7-8: select the first item and press "cancel"
        mouseListenerTest(modifiedChangedFilesList, true);
        //reset JOptionPane
        mockedJOptionPane.when(() -> JOptionPane.showConfirmDialog(any(), anyString(), anyString(),
                anyInt(), anyInt()))
                .thenReturn(JOptionPane.YES_OPTION);
        cancelButtonTest();

        //testcase step 9: file should have been staged
        loadComponents();
        assertEquals(1, modifiedChangedFilesList.getModel().getSize());
        firstItem = (AddCommitView.FileListItem) modifiedChangedFilesList.getModel().getElementAt(0);
        assertTrue(firstItem.isSelected());
        assertTrue(firstFile.isStaged());

        //testcase step 10: deselect item and cancel. File should have been unstaged
        mouseListenerTest(modifiedChangedFilesList, false);
        cancelButtonTest();
        assertFalse(firstFile.isStaged());
    }


}
