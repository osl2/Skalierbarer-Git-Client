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
import git.GitAuthor;
import levels.Level;
import settings.Data;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog to configure program settings
 */
public class SettingsDialogView implements IDialogView {
    private JPanel settingsPanel;
    private JComboBox<String> levelComboBox;
    private JTextField nameField;
    private JTextField eMailField;
    private JCheckBox tooltipsCheckbox;
    private JCheckBox treeViewCheckbox;
    private JButton saveButton;
    private GitAuthor author;
    private List<Level> levels;
    private JButton cancelButton;

    /**
     * Creates the DialogView and the needed ActionListeners.
     */
    public SettingsDialogView() {
        initDialogView();
        addActionListeners();
        setNameComponents();
    }

    /**
     * This method is needed in order to execute the GUI tests successfully.
     * Do not change otherwise tests might fail.
     */
    private void setNameComponents() {
        levelComboBox.setName("levelComboBox");
        nameField.setName("nameField");
        eMailField.setName("eMailField");
        tooltipsCheckbox.setName("tooltipsCheckbox");
        treeViewCheckbox.setName("treeViewCheckbox");
        saveButton.setName("saveButton");
        cancelButton.setName("cancelButton");
    }

    /**
     * This is used to initialize the entries in the DialogView.
     */
    private void initDialogView() {
        tooltipsCheckbox.setSelected(Settings.getInstance().useTooltips());
        treeViewCheckbox.setSelected(Settings.getInstance().showTreeView());
        author = Settings.getInstance().getUser();
        eMailField.setText(author.getEmail());
        nameField.setText(author.getName());
        levels = Data.getInstance().getLevels();
        String activeLevelName = Settings.getInstance().getLevel().getName();
        for (int i = 0; i < levels.size(); i++) {
            String level = levels.get(i).getName();
            levelComboBox.addItem(level);
            if (level.compareTo(activeLevelName) == 0) {
                levelComboBox.setSelectedIndex(i);
            }
        }
    }

    private void addActionListeners() {
        saveButton.addActionListener(e -> {
            Settings.getInstance().setUseTooltips(tooltipsCheckbox.isSelected());
            Settings.getInstance().setShowTreeView(treeViewCheckbox.isSelected());
            author.setEmail(eMailField.getText());
            author.setName(nameField.getText());
            Settings.getInstance().setUser(author);
            int index = levelComboBox.getSelectedIndex();
            Settings.getInstance().setLevel(levels.get(index));

            GUIController.getInstance().closeDialogView();
        });
        cancelButton.addActionListener(e -> GUIController.getInstance().closeDialogView());
    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Einstellungen";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(550, 400);
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return settingsPanel;
    }

    @Override
    public void update() {
        // This method is not used because it is not needed.
    }
}
