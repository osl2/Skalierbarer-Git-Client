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

import controller.GUIController;
import git.CredentialProviderHolder;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog to ask for the user's credentials when executing a
 * remote operation.
 */
public class UsernamePasswordDialogView implements IDialogView {
    @SuppressWarnings("unused")
    private JPanel panel1;
    private JPanel userPwPanel;
    private JTextField textField1;
    private JPasswordField passwordField;
    @SuppressWarnings("unused")
    private JLabel userNameLabel;
    private JButton okButton;
    @SuppressWarnings("unused")
    private JLabel pwLabel;
    private JButton breakButton;
    private final String name;


    @Override
    public String getTitle() {
        return "Benutzername - " + name;
    }

    /**
     * New Dialog
     * @param name Shown in dialog title to identify the credential usage
     */
    public UsernamePasswordDialogView(String name) {
        this.name = name;
        okButton.addActionListener(e -> {
            String username = textField1.getText();
            char[] password = passwordField.getPassword();
            StringBuilder pw = new StringBuilder();
            for (char c : password) {
                pw.append(c);
            }
            // Configures the Variables of the ProviderHolder
            CredentialProviderHolder.getInstance().setPassword(pw.toString());
            CredentialProviderHolder.getInstance().setUsername(username);
            CredentialProviderHolder.getInstance().setActive(true);
            GUIController.getInstance().closeDialogView();
        });
        breakButton.addActionListener(e -> {
            CredentialProviderHolder.getInstance().setActive(false);
            GUIController.getInstance().closeDialogView();
        });

        setNameComponents();
    }

    @Override
    public JPanel getPanel() {
        return userPwPanel;
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(400, 200);
    }

    @Override
    public void update() {
        // Is not needed for this dialogview
    }

    private void setNameComponents() {
        okButton.setName("okButton");
        breakButton.setName("breakButton");
        passwordField.setName("passwordField");
        userNameLabel.setName("userNameLabel");
    }
}
