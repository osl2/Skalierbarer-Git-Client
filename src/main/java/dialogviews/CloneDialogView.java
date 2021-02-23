package dialogviews;

import commands.Clone;
import controller.GUIController;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CloneDialogView implements IDialogView {
    private JPanel cloneDialog;
    private JTextField remoteField;
    private JButton chooseButton;
    private JCheckBox recursiveCheckBox;
    private JButton cancelButton;
    private JButton cloneButton;
    private Clone clone;
    private File path;
    private JFileChooser chooser;

    public CloneDialogView(String gitUrl, File file, boolean recursive) {
        remoteField.setText(gitUrl);
        path = file;
        recursiveCheckBox.setSelected(recursive);
        addActionlistener();
    }

    public CloneDialogView() {
        addActionlistener();
    }

    private void addActionlistener() {
        chooseButton.addActionListener(e -> {
            chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(cloneDialog);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                path = chooser.getSelectedFile();
            }
        });
        cloneButton.addActionListener(e -> {
            clone = new Clone();
            clone.setGitURL(remoteField.getText());
            clone.cloneRecursive(recursiveCheckBox.isSelected());
            clone.setDestination(path);
            boolean success;
            success = clone.execute();
            if (success) {
                GUIController.getInstance().setCommandLine(clone.getCommandLine());
                GUIController.getInstance().closeDialogView();
            }
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
        return "Clone";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(500, 400);
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return cloneDialog;
    }

    public void update() {

    }

}
