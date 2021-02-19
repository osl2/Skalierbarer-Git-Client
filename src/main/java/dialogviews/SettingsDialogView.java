package dialogviews;

import controller.GUIController;
import git.GitAuthor;
import levels.Level;
import settings.Data;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class SettingsDialogView implements IDialogView {
    private JPanel settingsPanel;
    private JComboBox<String> levelComboBox;
    private JTextField nameField;
    private JTextField eMailField;
    private JCheckBox tooltipsCheckbox;
    private JCheckBox treeViewCheckbox;
    private JButton saveButton;
    private GitAuthor author;
    private LinkedList<Level> levels;
    private JButton cancelButton;

    /**
     * Creates the DialogView and the needed ActionListeners.
     */
    public SettingsDialogView() {
        initDialogView();
        addActionListeners();
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
            if (Settings.getInstance().settingsChanged()) {
                Settings.getInstance().fireDataChangedEvent();
            }
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    public void update() {

    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(11, 5, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Kompetenzstufe");
        settingsPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        settingsPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 25), new Dimension(-1, 30), new Dimension(-1, 35), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        settingsPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 11, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        levelComboBox = new JComboBox();
        settingsPanel.add(levelComboBox, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Git Name");
        settingsPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        settingsPanel.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 4), new Dimension(-1, 5), new Dimension(-1, 6), 0, false));
        nameField = new JTextField();
        settingsPanel.add(nameField, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Git Email");
        settingsPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eMailField = new JTextField();
        settingsPanel.add(eMailField, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tooltipsCheckbox = new JCheckBox();
        tooltipsCheckbox.setText("Hilfstexte einblenden");
        settingsPanel.add(tooltipsCheckbox, new com.intellij.uiDesigner.core.GridConstraints(7, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        treeViewCheckbox = new JCheckBox();
        treeViewCheckbox.setText("Baumansicht einblenden");
        settingsPanel.add(treeViewCheckbox, new com.intellij.uiDesigner.core.GridConstraints(8, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Speichern");
        settingsPanel.add(saveButton, new com.intellij.uiDesigner.core.GridConstraints(9, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Abbrechen");
        settingsPanel.add(cancelButton, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        settingsPanel.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(10, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(20, 20), new Dimension(25, 25), new Dimension(30, 30), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        settingsPanel.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(20, 20), new Dimension(25, 25), new Dimension(30, 30), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        settingsPanel.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 11, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer7 = new com.intellij.uiDesigner.core.Spacer();
        settingsPanel.add(spacer7, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 5), new Dimension(-1, 10), new Dimension(-1, 15), 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return settingsPanel;
    }
}
