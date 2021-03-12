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

import commands.Remote;
import controller.GUIController;
import views.RemoteView;

import javax.swing.*;
import java.awt.*;

/**
 * Dialogview opened to add a new Remote
 */
public class RemoteAddDialogView implements IDialogView {
    @SuppressWarnings("unused")
    private JPanel panel1;
    private JPanel remoteAddPanel;
    private JTextField namefield;
    private JTextField urlField;
    private JButton stopButton;
    private JButton addButton;
    @SuppressWarnings("unused")
    private JLabel nameLabel;
    @SuppressWarnings("unused")
    private JLabel urlLabel;

    /**
     * Create a new Dialog.
     */
    public RemoteAddDialogView() {
        testGUI();
        stopButton.addActionListener(e -> GUIController.getInstance().closeDialogView());
        addButton.addActionListener(e -> {
            String name = namefield.getText();
            if (name.compareTo("") == 0) {
                GUIController.getInstance().errorHandler("Kein Name eingegeben");
                return;
            }
            Remote rem = new Remote();
            rem.setRemoteSubcommand(Remote.RemoteSubcommand.ADD);
            rem.setRemoteName(name);
            rem.setUrl(urlField.getText());
            if (rem.execute()) {
                GUIController.getInstance().setCommandLine(rem.getCommandLine());
                GUIController.getInstance().closeDialogView();
                GUIController.getInstance().openView(new RemoteView());
            }
        });
    }

    @Override
    public String getTitle() {
        return "Remote hinzufügen";
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(300, 150);
    }

    @Override
    public JPanel getPanel() {
        return remoteAddPanel;
    }

    @Override
    public void update() {
        //Not needed for this class
    }

    /**
     * Is for testing the GUI. Do not change this!!!!!!
     */
    private void testGUI() {
        namefield.setName("namefield");
        urlField.setName("urlField");
        addButton.setName("addButton");
        stopButton.setName("stopButton");
    }
}
