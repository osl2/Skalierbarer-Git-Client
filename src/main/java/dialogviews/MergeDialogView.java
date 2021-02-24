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
import java.util.logging.Logger;

/**
 * This dialog is used to select the Branch which should be merged into the current branch.
 */
public class MergeDialogView implements IDialogView {

    private JComboBox<GitBranch> fromComboBox;
    private JButton okButton;
    private JButton abortButton;
    private JLabel fromLabel;
    private JLabel toLabel;
    private JLabel toValueLabel;
    private JPanel contentPane;
    private final GitData data;

    public MergeDialogView() {
        this.data = new GitData();

        this.fromLabel.setText("Von");
        this.toLabel.setText("Auf");
        this.okButton.setText("Merge");
        this.abortButton.setText("Abbrechen");

        this.abortButton.addActionListener(e -> GUIController.getInstance().closeDialogView());
        this.okButton.addActionListener(this::okButtonListener);


        fromComboBox.setRenderer((jList, gitBranch, i, b, b1) -> {
            if (gitBranch != null)
                return new JLabel(gitBranch.getName());
            else
                return new JLabel("");
        });

        // To update the branch list
        update();


    }

    /**
     * OnClick listener for the okButton
     *
     * @param actionEvent the fired event - wont be used
     */
    private void okButtonListener(ActionEvent actionEvent) {
        try {
            if (fromComboBox.getSelectedItem() == null || data.getSelectedBranch() == null) {
                GUIController.getInstance().errorHandler("Es müssen zwei Branches ausgewählt werden");
                return;
            }

            // Close Merge Dialog as the user interaction for this view is complete
            GUIController.getInstance().closeDialogView();
            Merge merge = new Merge((GitBranch) fromComboBox.getSelectedItem(), data.getSelectedBranch());
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

    @Override
    public void update() {
        DefaultComboBoxModel<GitBranch> cbModel = new DefaultComboBoxModel<>();
        try {
            GitBranch selectedBranch = data.getSelectedBranch();
            data.getBranches().stream()
                    .filter(b -> !b.equals(selectedBranch)) // dont allow merging into itself
                    .forEach(cbModel::addElement);
            fromComboBox.setModel(cbModel);
            this.toValueLabel.setText(selectedBranch.getName());

        } catch (GitException e) {
            GUIController.getInstance().errorHandler("Der aktuelle Branch konnte nicht ausgelesen werden.");
            Logger.getGlobal().warning(e.getMessage());
        } catch (IOException e) {
            GUIController.getInstance().errorHandler("Die Liste der Branches konnte nicht abgefragt werden.");
            Logger.getGlobal().warning(e.getMessage());
        }

    }
}
