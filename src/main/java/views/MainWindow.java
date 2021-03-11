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

import controller.GUIController;
import dialogviews.SettingsDialogView;
import git.GitData;
import settings.Data;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * The main window of the application tying all submodules together.
 */
public class MainWindow extends JFrame {

    private IView loadedView;
    // Main-Panel
    private JPanel contentPane;
    // Panel containing our button row
    private JPanel buttonPanel;
    // Replacable panel
    private JPanel viewPanel;
    @SuppressWarnings("unused")
    private JPanel headPanel;
    private JTextField commandLineTextField;
    private JLabel branchLabel;

    public MainWindow() {
        setNameComponents();
    }

    /**
     * Returns the content panel of the MainWindow
     *
     * @return The content panel that holds all components
     */
    public JPanel getPanel() {
        return contentPane;
    }

    /**
     * Replace the lower view of the window.
     * This will invoke {@link #update()}
     *
     * @param view the IView to show
     * @return true if view was able to be changed
     */
    public boolean setView(IView view) {
        this.loadedView = view;
        // New Data -> Refresh view
        this.update();
        return true;
    }

    /*
    This is for testing
     */
    private void setNameComponents() {
        commandLineTextField.setName("commandLineTextField");
        branchLabel.setName("branchLabel");
        buttonPanel.setName("buttonPanel");
        viewPanel.setName("viewPanel");
    }

    /**
     * Sets the text shown to the user in the command line representation
     * @param text the text to show
     */
    public void setCommandLineText(String text) {
        this.commandLineTextField.setText(text);
    }

    /**
     * Removes all buttons from the Button panel at the top
     */
    public void clearButtonPanel() {
        for (Component c : buttonPanel.getComponents()) {
            this.buttonPanel.remove(c);
        }
    }

    /**
     * Get the lower part of the window
     *
     * @return the IView loaded into the lower part of the window
     */
    public IView getView() {
        return this.loadedView;
    }


    /**
     * Refreshes all dynamic parts of the Window. Including the MenuBar and loaded View
     */
    @SuppressWarnings("BoundFieldAssignment")
    public void update() {
        if (this.loadedView != null) {
            this.contentPane.remove(viewPanel);
            this.viewPanel = this.loadedView.getView();
            this.contentPane.add(viewPanel);
            this.viewPanel.revalidate();
            this.viewPanel.repaint();
        }

        try {
            this.branchLabel.setText(new GitData().getSelectedBranch().getFullName());
        } catch (IOException e) {
            this.branchLabel.setText("");
            Logger.getGlobal().warning("GIT DATA RETURNED NO BRANCH");
        }

        // Refresh menubar
        createMenubar();

        // Enable or disable Tooltips
        ToolTipManager.sharedInstance().setEnabled(Settings.getInstance().useTooltips());
    }

    /**
     * Add a new Button to the ButtonRow in the top
     *
     * @param text     The Button Text
     * @param toolTip  The Tooltip to be shown if {@link Settings#useTooltips()} returns true
     * @param listener The ActionListener to be called when the Button is clicked.
     */
    public void addButton(String text, String toolTip, ActionListener listener) {
        JButton newButton = new JButton(text);
        newButton.setToolTipText(toolTip);
        newButton.addActionListener(listener);
        this.buttonPanel.add(newButton);
        this.buttonPanel.repaint();
    }

    /**
     * Open the Window. This invokes {@link #update()}
     */
    public void open() {
        this.update();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(this.contentPane);
        this.setPreferredSize(new Dimension(1280, 720));
        this.pack();
        this.setVisible(true);
    }

    private void changeRepository(File path) {
        Settings.getInstance().setActiveRepositoryPath(path);
        new GitData().reinitialize();
        GUIController.getInstance().update();
    }

    private void openFileListener() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int response = dirChooser.showOpenDialog(this);

        if (response != JFileChooser.APPROVE_OPTION)
            return;

        File newDirectory = dirChooser.getSelectedFile();
        if (!(new File(newDirectory, ".git")).isDirectory()) {
            GUIController.getInstance().errorHandler(dirChooser.getSelectedFile() + " ist kein valides Git Repository");
            return;
        }

        changeRepository(newDirectory);

    }

    private void createMenubar() {
        JMenuBar bar = new JMenuBar();
        JMenu m1 = new JMenu("Repository");
        JMenu recentlyUsed = new JMenu("Kürzlich Verwendet");

        for (File f : Data.getInstance().getRecentlyOpenedRepositories()) {
            if (!f.isDirectory()) continue;

            JMenuItem repo = new JMenuItem(f.getPath());
            repo.addActionListener(e -> changeRepository(f));
            recentlyUsed.add(repo);
        }

        JMenuItem openItem = new JMenuItem("Öffnen");

        openItem.addActionListener(l -> openFileListener());

        m1.add(openItem);
        m1.add(recentlyUsed);

        JMenuItem settingsItem = new JMenuItem("Einstellungen");
        settingsItem.addActionListener(e -> GUIController.getInstance().openDialog(new SettingsDialogView()));
        m1.add(settingsItem);

        bar.add(m1);
        this.setJMenuBar(bar);
    }
}
