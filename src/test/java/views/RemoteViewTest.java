package views;

import commands.AbstractRemoteTest;
import commands.Remote;
import dialogviews.FindComponents;
import git.GitRemote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;

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
        FindComponents find = new FindComponents();
        RemoteView remoteD = new RemoteView();
        JPanel frame = remoteD.getView();
        urlField = (JTextField) find.getChildByName(frame, "urlField");
        assertNotNull(urlField);
        nameField = (JTextField) find.getChildByName(frame, "nameField");
        assertNotNull(nameField);
        branchArea = (JTextArea) find.getChildByName(frame, "branchArea");
        assertNotNull(branchArea);
        remoteList = (JList<GitRemote>) find.getChildByName(frame, "remoteList");
        assertNotNull(remoteList);
        safeButton = (JButton) find.getChildByName(frame, "safeButton");
        assertNotNull(safeButton);
        deleteButton = (JButton) find.getChildByName(frame, "deleteButton");
        assertNotNull(deleteButton);
        addButton = (JButton) find.getChildByName(frame, "addButton");
        assertNotNull(addButton);
        removeButton = (JButton) find.getChildByName(frame, "removeButton");
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
        deleteButton.getActionListeners()[0].actionPerformed(new ActionEvent(deleteButton, ActionEvent.ACTION_PERFORMED, null));
        assertEquals(0, remoteList.getModel().getSize());
    }
    @Test
    void testMainWindowThings(){
        RemoteView rView = new RemoteView();
        Remote rem = new Remote();
        assertNotNull(rView.getView());
        assertEquals("Remote", rem.getName());
        rem.setRemoteSubcommand(Remote.RemoteSubcommand.ADD);
        rem.setRemoteName("hh");
        rem.setUrl("gg");
        assertNotNull(rem.getCommandLine());
        assertNotNull(rem.getDescription());

    }
}
