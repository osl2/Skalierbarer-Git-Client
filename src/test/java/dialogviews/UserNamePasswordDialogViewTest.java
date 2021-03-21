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
import git.CredentialProviderHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class UserNamePasswordDialogViewTest extends AbstractCommandTest {
    private final String NAME = "Name";
    private UsernamePasswordDialogView usernamePasswordDialogView;
    private JButton breakButton;
    private JButton okButton;
    private JPasswordField passwordField;
    private JLabel userNameLabel;

    @BeforeEach
    void prepare() {
        usernamePasswordDialogView = new UsernamePasswordDialogView(NAME);
        JPanel panel = usernamePasswordDialogView.getPanel();
        breakButton = (JButton) FindComponents.getChildByName(panel, "breakButton");
        okButton = (JButton) FindComponents.getChildByName(panel, "okButton");
        passwordField = (JPasswordField) FindComponents.getChildByName(panel, "passwordField");
        userNameLabel = (JLabel) FindComponents.getChildByName(panel, "userNameLabel");
    }

    @Test
    void testDialogData() {
        assertEquals(0, usernamePasswordDialogView.getTitle().compareTo("Benutzername - " + NAME));
        assertEquals(400, usernamePasswordDialogView.getDimension().width);
        assertEquals(200, usernamePasswordDialogView.getDimension().height);
    }

    @Test
    void okButtonTest() {
        String testUserName = "testUserName";
        String testPassword = "testPassword";
        assertNotNull(userNameLabel);
        assertNotNull(passwordField);
        userNameLabel.setText(testUserName);
        passwordField.setText(testPassword);
        assertNotNull(okButton);
        for (ActionListener listener : okButton.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(okButton, ActionEvent.ACTION_PERFORMED, "OK button clicked"));
        }
        assertTrue(CredentialProviderHolder.getInstance().isActive());
        assertTrue(guiControllerTestable.closeDialogViewCalled);
    }

    @Test
    void breakButtonTest() {
        assertNotNull(breakButton);
        for (ActionListener listener : breakButton.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(breakButton, ActionEvent.ACTION_PERFORMED, "Break button clicked"));
        }
        assertFalse(CredentialProviderHolder.getInstance().isActive());
        assertTrue(guiControllerTestable.closeDialogViewCalled);
    }
}
