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
package dialogviews;

import commands.AbstractCommandTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemoteAddDialogViewTest extends AbstractCommandTest {
    JTextField textField;
    JTextField urlField;
    JButton addButton;
    JButton stopButton;

    @BeforeEach
    void getComp() {
        RemoteAddDialogView remoteD = new RemoteAddDialogView();
        JPanel frame = remoteD.getPanel();
        textField = (JTextField) FindComponents.getChildByName(frame, "namefield");
        assertNotNull(textField);
        urlField = (JTextField) FindComponents.getChildByName(frame, "urlField");
        assertNotNull(urlField);
        addButton = (JButton) FindComponents.getChildByName(frame, "addButton");
        assertNotNull(addButton);
        stopButton = (JButton) FindComponents.getChildByName(frame, "stopButton");
        assertNotNull(stopButton);
    }
    @Test
    void testRemoteAddDialogView1()  {
        textField.setText("");
        addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }
    @Test
    void testRemoteAddNoUrl() {
        textField.setText("test");
        urlField.setText("");
        addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.errorHandlerECalled);
    }
    @Test
    void stopButtonTest(){
        stopButton.getActionListeners()[0].actionPerformed(new ActionEvent(stopButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.closeDialogViewCalled);
    }
    @Test
    void addButtonTest(){
        textField.setText("abc");
        urlField.setText("abc");
        addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.setCommandLineCalled);
        textField.setText("abc");
        urlField.setText("abc");
        addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }
    @Test
    void titleTest(){
        RemoteAddDialogView view = new RemoteAddDialogView();
        view.update();
        assertNotNull(view.getTitle());
        assertNotNull(view.getDimension());
        assertNotNull(view.getPanel());

    }
}
