package views;

import commands.AbstractRemoteTest;
import commands.Remote;
import dialogviews.FindComponents;
import git.GitRemote;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class RemoteViewTest extends AbstractRemoteTest {
    JTextField urlField;
    JTextField nameField;
    JTextArea branchArea;
    JList<GitRemote> remoteList;
    JButton safeButton;
    JButton deleteButton;
    JButton addButton;
    JButton removeButton;
    @BeforeEach
    void prepare(){
        RemoteView remoteD = new RemoteView();
        JPanel frame = remoteD.getView();
        urlField = (JTextField) FindComponents.getChildByName(frame, "urlField");
        assertNotNull(urlField);
        nameField = (JTextField) FindComponents.getChildByName(frame, "nameField");
        assertNotNull(nameField);
        branchArea = (JTextArea) FindComponents.getChildByName(frame, "branchArea");
        assertNotNull(branchArea);
        remoteList = (JList<GitRemote>) FindComponents.getChildByName(frame, "remoteList");
        assertNotNull(remoteList);
        safeButton = (JButton) FindComponents.getChildByName(frame, "safeButton");
        assertNotNull(safeButton);
        deleteButton = (JButton) FindComponents.getChildByName(frame, "deleteButton");
        assertNotNull(deleteButton);
        addButton = (JButton) FindComponents.getChildByName(frame, "addButton");
        assertNotNull(addButton);
        removeButton = (JButton) FindComponents.getChildByName(frame, "removeButton");
    }
    @Test
    void testStop(){
        removeButton.getActionListeners()[0].actionPerformed(new ActionEvent(removeButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.restoreDefaultViewCalled);
    }
    @Test
    void testAdd(){
        addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.openDialogCalled);
    }
    @Test
    void containsOrigin(){
        int size = remoteList.getModel().getSize();
        assertEquals( 1, size);
        String url = remoteList.getModel().getElementAt(0).getUrl();
        remoteList.setSelectedIndex(0);
        assertEquals(url,urlField.getText());
        assertEquals("master" + System.lineSeparator(), branchArea.getText() );
        safeButton.getActionListeners()[0].actionPerformed(new ActionEvent(safeButton, ActionEvent.ACTION_PERFORMED, null));
        assertEquals("master" + System.lineSeparator(), branchArea.getText() );
        assertEquals(url, remoteList.getSelectedValue().getUrl());
        urlField.setText("");
        safeButton.getActionListeners()[0].actionPerformed(new ActionEvent(safeButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.errorHandlerECalled);
        deleteButton.getActionListeners()[0].actionPerformed(new ActionEvent(deleteButton, ActionEvent.ACTION_PERFORMED, null));
        assertEquals(0, remoteList.getModel().getSize());
    }
    @Test
    void testMainWindowThings(){
        deleteButton.getActionListeners()[0].actionPerformed(new ActionEvent(deleteButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
        safeButton.getActionListeners()[0].actionPerformed(new ActionEvent(safeButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
        RemoteView rView = new RemoteView();
        rView.update();
        Remote rem = new Remote();
        rem.onButtonClicked();
        assertTrue(guiControllerTestable.openViewCalled);
        rem.setRemoteSubcommand(Remote.RemoteSubcommand.INACTIVE);
        assertNull(rem.getCommandLine());
        assertNotNull(rView.getView());
        assertEquals("Remote", rem.getName());
        rem.setRemoteSubcommand(Remote.RemoteSubcommand.ADD);
        rem.setRemoteName("hh");
        rem.setUrl("gg");
        assertNotNull(rem.getCommandLine());
        assertNotNull(rem.getDescription());
    }
    @Test
    void testSetUrl() throws GitAPIException {

        File newDir = new File("src" + System.getProperty("file.separator") + "test" +
                System.getProperty("file.separator") + "resources" + System.getProperty("file.separator") + "Test" +
                System.getProperty("file.separator") + "testRemote");
        Git.init().setDirectory(newDir).setBare(false).call();
        int size = remoteList.getModel().getSize();
        assertEquals( 1, size);
        remoteList.setSelectedIndex(0);
        urlField.setText(newDir.getPath());
        safeButton.getActionListeners()[0].actionPerformed(new ActionEvent(safeButton, ActionEvent.ACTION_PERFORMED, null));
        assertEquals(newDir.getPath(), remoteList.getSelectedValue().getUrl());
        deleteDir(newDir);
    }
}
