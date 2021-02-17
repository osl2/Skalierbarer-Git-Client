package dialogviews;

import commands.Merge;
import controller.GUIController;
import git.GitBranch;
import git.GitData;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * This dialog is used to select the Branch which should be merged into the current branch.
 */
public class MergeDialogView implements IDialogView {

    private Merge merge;
    private JComboBox<GitBranch> fromComboBox;
    private JButton okButton;
    private JButton abortButton;
    private JLabel fromLabel;
    private JLabel toLabel;
    private JLabel toValueLabel;
    private JPanel contentPane;
    private GitData data;

    public MergeDialogView() {
        try {
            this.data = new GitData();
            GitBranch selectedBranch = data.getSelectedBranch();
            this.fromLabel.setText("Von");
            this.toLabel.setText("Auf");
            this.okButton.setText("Merge");
            this.abortButton.setText("Abbrechen");
            this.toValueLabel.setText(selectedBranch.getName());

            this.abortButton.addActionListener(e -> GUIController.getInstance().closeDialogView());
            this.okButton.addActionListener(this::okButtonListener);

            DefaultComboBoxModel<GitBranch> cbModel = new DefaultComboBoxModel<>();

            data.getBranches().stream()
                    .filter(b -> !b.equals(selectedBranch)) // dont allow merging into itself
                    .forEach(cbModel::addElement);

            fromComboBox.setRenderer((jList, gitBranch, i, b, b1) -> new JLabel(gitBranch.getName()));
            fromComboBox.setModel(cbModel);


        } catch (GitException | IOException e) {
            GUIController.getInstance().errorHandler(e);
        }


    }

    /**
     * OnClick listener for the okButton
     *
     * @param actionEvent the fired event - wont be used
     */
    private void okButtonListener(ActionEvent actionEvent) {
        try {
            this.merge = new Merge((GitBranch) fromComboBox.getSelectedItem(), data.getSelectedBranch());
            if (!merge.execute()) {
                GUIController.getInstance().errorHandler("Merge fehlgeschlagen");
            }
        } catch (IOException e) {
            GUIController.getInstance().errorHandler(e);
        }
    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Merge: Zweig auswählen";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return this.contentPane.getPreferredSize();
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return this.contentPane;
    }

    public void update() {

    }
}
