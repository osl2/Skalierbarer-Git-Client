package dialogviews;

import javax.swing.*;
import java.awt.*;

public class SettingsDialogView implements IDialogView {
    private JPanel settingsPanel;
    private JComboBox levelComboBox;
    private JTextField nameField;
    private JTextField eMailField;
    private JCheckBox tooltipsCheckbox;
    private JCheckBox treeViewCheckbox;
    private JButton saveButton;
    private JButton cancelButton;

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return null;
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return null;
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return null;
    }

    public void update() {

    }
}
