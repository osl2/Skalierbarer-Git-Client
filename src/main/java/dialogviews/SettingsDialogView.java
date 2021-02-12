package dialogviews;

import controller.GUIController;
import git.GitAuthor;
import levels.Level;
import settings.Data;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class SettingsDialogView implements IDialogView {
    private JPanel settingsPanel;
    private JComboBox levelComboBox;
    private JTextField nameField;
    private JTextField eMailField;
    private JCheckBox tooltipsCheckbox;
    private JCheckBox treeViewCheckbox;
    private JButton saveButton;
    private GitAuthor author;
    private LinkedList<Level> levels;
    private JButton cancelButton;

    /**
     * Creates the DialogView and the needed Actionlisteners.
     */
    public SettingsDialogView() {
        initDialogView();
        addActionlisteners();
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
        for(int i = 0; i < levels.size(); i++) {
            String level = levels.get(i).getName();
            levelComboBox.addItem(level);
            if(level.compareTo(activeLevelName) == 0) {
                levelComboBox.setSelectedIndex(i);
            }
        }
    }

    private void addActionlisteners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings.getInstance().setUseTooltips(tooltipsCheckbox.isSelected());
                Settings.getInstance().setShowTreeView(treeViewCheckbox.isSelected());
                author.setEmail(eMailField.getText());
                author.setName(nameField.getText());
                Settings.getInstance().setUser(author);
                int index = levelComboBox.getSelectedIndex();
                Settings.getInstance().setLevel(levels.get(index));
                //This is used to apply the Settings changes;
                Settings.getInstance().settingsChanged();
                GUIController.getInstance().closeDialogView();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUIController.getInstance().closeDialogView();
            }
        });
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
        Dimension dim = new Dimension(550, 400);
        return dim;
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

    public void update() {

    }
}
