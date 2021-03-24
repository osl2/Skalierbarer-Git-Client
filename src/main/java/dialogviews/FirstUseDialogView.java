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

import commands.Config;
import commands.Init;
import controller.GUIController;
import git.GitData;
import git.exception.*;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * This dialog is opened when the user has no valid configuration.
 * It asks for basic data like Author information and a repo to open/initialize.
 */
public class FirstUseDialogView implements IDialogView {
    private JTextField nameField;
    private JTextField eMailField;
    private JButton chooseButton;
    private JButton finishButton;
    private JPanel firstUseDialog;
    private String name = null;
    private String eMail = null;
    private File path = null;
    private Init init;
    private Config config;
    private JFileChooser chooser;


    /**
     * Creates a new instance. Needs to be opened by a GUIController.
     *
     * @see GUIController#openDialog(IDialogView)
     */
    public FirstUseDialogView() {
        finishButton.addActionListener(e -> {
            name = nameField.getText();
            eMail = eMailField.getText();
            config = new Config();
            if (name.isEmpty() || eMail.isEmpty()){
                GUIController.getInstance().errorHandler(new GitException("Name oder Email leer"));
            } else {
                config.setName(name);
                config.setEMail(eMail);
                init = new Init();
                init.setPathToRepository(path);
                if (init.execute()) {
                    Settings.getInstance().setActiveRepositoryPath(path);
                    // make sure it is initialized.
                    GitData data = new GitData();
                    data.reinitialize();
                    config.execute();
                    GUIController.getInstance().closeDialogView();
                }
            }
        });
        // Opens a new JFileChooser to set a path to a directory.
        chooseButton.addActionListener(e -> {
            chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                path = chooser.getSelectedFile();
            }
        });

        setNameComponents();
    }

    /*
    This is for testing
     */
    private void setNameComponents() {
        chooseButton.setName("chooseButton");
        finishButton.setName("finishButton");
    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Erstbenutzung";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(500, 150);
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return firstUseDialog;
    }

    @Override
    public void update() {
        // This method is not used because it is not needed.
    }

}
