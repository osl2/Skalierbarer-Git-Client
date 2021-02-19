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

public class MainWindow extends JFrame {

    private IView loadedView;
    // Main-Panel
    private JPanel contentPane;
    // Panel containing our button row
    private JPanel buttonPanel;
    // Replacable panel
    private JPanel viewPanel;
    private JPanel headPanel;
    private JTextField commandLineTextField;
    private JLabel branchLabel;

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

    public void setCommandLineText(String text) {
        this.commandLineTextField.setText(text);
    }

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
        Settings.getInstance().fireDataChangedEvent();
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
        JMenu m2 = new JMenu("Über");
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
        //m1.add(new JMenuItem("Initialisieren")); // removed as it has got it's own button in the gui right now
        //m1.add(new JMenuItem("Klonen")); // Also has a button apparently.
        m1.add(recentlyUsed);

        JMenuItem settingsItem = new JMenuItem("Einstellungen");
        settingsItem.addActionListener(e -> GUIController.getInstance().openDialog(new SettingsDialogView()));
        m1.add(settingsItem);

        bar.add(m1);
        bar.add(m2);
        this.setJMenuBar(bar);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setMinimumSize(new Dimension(1280, 720));
        viewPanel = new JPanel();
        viewPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        viewPanel.setBackground(new Color(-4521838));
        viewPanel.setOpaque(true);
        viewPanel.setToolTipText("");
        contentPane.add(viewPanel, BorderLayout.CENTER);
        headPanel = new JPanel();
        headPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(headPanel, BorderLayout.NORTH);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        headPanel.add(buttonPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        headPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(600, 11), null, 0, false));
        commandLineTextField = new JTextField();
        commandLineTextField.setEditable(false);
        commandLineTextField.setEnabled(true);
        headPanel.add(commandLineTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(450, -1), null, 0, false));
        branchLabel = new JLabel();
        branchLabel.setText("Label");
        headPanel.add(branchLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
