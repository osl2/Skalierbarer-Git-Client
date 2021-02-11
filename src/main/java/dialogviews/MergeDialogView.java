package dialogviews;

import commands.Merge;
import controller.GUIController;
import git.GitBranch;
import git.GitData;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
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
  private GitData data;

  public MergeDialogView() {
    try {
      this.data = new GitData();
      this.fromLabel.setText("Von");
      this.toLabel.setText("Auf");
      this.toValueLabel.setText(data.getSelectedBranch().getName());

      DefaultComboBoxModel<GitBranch> cbModel = new DefaultComboBoxModel<>();
      data.getBranches().forEach(cbModel::addElement);

      fromComboBox.setModel(cbModel);


    } catch (GitException | IOException e) {
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
