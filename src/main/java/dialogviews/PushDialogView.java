package dialogviews;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import controller.GUIController;
import git.GitBranch;
import git.GitData;
import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;

public class PushDialogView implements IDialogView {

  private JPanel contentPane;
  private JComboBox remoteComboBox;
  private JComboBox remoteBranchComboBox;
  private JCheckBox setUpstreamCheckbox;
  private JButton pushButton;
  private JPanel remotePanel;
  private JButton refreshButton;
  private JComboBox<GitBranch> localBranchComboBox;
  private GitData gitData;

  public PushDialogView() {
    //initialize combo box with local branches
    /*
    localBranchComboBox.setRenderer(new BranchComboBoxRenderer());
    gitData = new GitData();
    List<GitBranch> localBranches;
    try {
      localBranches = gitData.getBranches();
      DefaultComboBoxModel<GitBranch> localBranchComboBoxModel = new DefaultComboBoxModel();
      localBranchComboBoxModel.addAll(localBranches);
      localBranchComboBox.setModel(localBranchComboBoxModel);
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e);
      GUIController.getInstance().closeDialogView();
    }

     */
    DefaultComboBoxModel<GitBranch> model = new DefaultComboBoxModel<>();
    gitData = new GitData();
    List<GitBranch> localBranches = null;
    try {
      localBranches = gitData.getBranches();
    } catch (GitException e) {
      e.printStackTrace();
    }
    //TODO: wieso schlägt es bei model.addAll() fehl??????????
    //model.addAll(localBranches);
    for (GitBranch branch : localBranches){
      model.addElement(branch);
    }
    localBranchComboBox.setModel(model);

  }

  /**
   * DialogWindow Title
   *
   * @return Window Title as String
   */
  @Override
  public String getTitle() {
    return "Push";
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
    //TODO
  }

  private void createUIComponents() {
    localBranchComboBox = new JComboBox<>();
    localBranchComboBox.setRenderer(new BranchComboBoxRenderer());
  }


  private class BranchComboBoxRenderer extends JTextField implements ListCellRenderer<GitBranch> {


    @Override
    public Component getListCellRendererComponent(JList<? extends GitBranch> list, GitBranch value, int index, boolean isSelected, boolean cellHasFocus) {
      GitBranch branch = (GitBranch) value;
      this.setText(branch.getName());
      return this;
    }
  }
}


