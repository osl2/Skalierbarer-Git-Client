package dialogviews;

import commands.Clone;
import controller.GUIController;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * This class represents a DialogView to execute the {@link Clone} command with the GUI.
 * You can either clone a local repository by passing the path instead the git url or
 * clone a online Repository by passing a valid git url.
 */
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

    /**
     * Creates a new CloneDialogView window with preset values.
     *
     * @param gitUrl    the git url which leads to the remote.
     * @param file      the directory in which the remote should be cloned in.
     * @param recursive if true the remote repository will be cloned recursive.
     */
    public CloneDialogView(String gitUrl, File file, boolean recursive) {
        remoteField.setText(gitUrl);
        path = file;
        recursiveCheckBox.setSelected(recursive);
        addActionlistener();
        setNameComponents();
    }

    /**
     * Creates a new CloneDialogView window without preset values
     */
    public CloneDialogView() {
        setNameComponents();
        addActionlistener();
    }

    /**
     * This method is needed in order to execute the GUI tests successfully.
     * Do not change otherwise tests might fail.
     */
    private void setNameComponents() {
        remoteField.setName("remoteField");
        recursiveCheckBox.setName("recursiveCheckBox");
        cloneButton.setName("cloneButton");
        cancelButton.setName("cancelButton");
        chooseButton.setName("chooseButton");
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

    @Override
    public void update() {
        // This method is not used because it is not needed.
    }

}
