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

import commands.AbstractRemoteTest;
import commands.Remote;
import dialogviews.FindComponents;
import dialogviews.RemoteAddDialogView;
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
        urlField.setText(newDir.getAbsolutePath());
        safeButton.getActionListeners()[0].actionPerformed(new ActionEvent(safeButton, ActionEvent.ACTION_PERFORMED, null));
        assertEquals(newDir.getAbsolutePath(), remoteList.getSelectedValue().getUrl());
        deleteDir(newDir);
    }
    @Test
    void globalRemoteTest() throws GitAPIException {
        File newDir = new File("src" + System.getProperty("file.separator") + "test" +
                System.getProperty("file.separator") + "resources" + System.getProperty("file.separator") + "Test" +
                System.getProperty("file.separator") + "testRemote");
        Git.init().setDirectory(newDir).setBare(false).call();
        int size = remoteList.getModel().getSize();
        assertEquals( 1, size);
        remoteList.setSelectedIndex(0);
        assertNotNull(nameField.getText());
        assertNotNull(urlField.getText());
        assertNotNull(branchArea.getText());
        addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.openDialogCalled);
        RemoteAddDialogView addDialogView = new RemoteAddDialogView();
        JPanel addDialogPanel = addDialogView.getPanel();
        JTextField addNameField = (JTextField) FindComponents.getChildByName(addDialogPanel, "namefield");
        assertNotNull(addNameField);
        JTextField addUrlField = (JTextField) FindComponents.getChildByName(addDialogPanel, "urlField");
        assertNotNull(addUrlField);
        JButton addRemAddButton = (JButton) FindComponents.getChildByName(addDialogPanel, "addButton");
        assertNotNull(addRemAddButton);
        addNameField.setText("test");
        addUrlField.setText(newDir.getAbsolutePath());
        addRemAddButton.getActionListeners()[0].actionPerformed(new ActionEvent(addRemAddButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.closeDialogViewCalled);
        prepare();
        assertEquals(2, remoteList.getModel().getSize());
        remoteList.setSelectedIndex(1);
        assertEquals("test", nameField.getText());
        assertEquals(newDir.getAbsolutePath(), urlField.getText());
        deleteDir(newDir);
    }
}
