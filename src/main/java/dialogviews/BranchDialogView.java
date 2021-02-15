package dialogviews;

import commands.Branch;
import controller.GUIController;
import git.GitBranch;
import git.GitCommit;
import git.GitData;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BranchDialogView implements IDialogView {
  private List<GitBranch> branches ;
  private LinkedList<GitCommit> commits = new LinkedList<GitCommit>();
  private JPanel panel1;
  private JPanel BranchPanel;
  private JComboBox branchComboBox;
  private JComboBox commitComboBox;
  private JTextField nameField;
  private JButton branchButton;
  private JLabel branchLabel;
  private JLabel commitLabel;
  private JLabel nameLabel;
  private GitData gitData = new GitData();


  public BranchDialogView(){
    commitComboBox.setRenderer(new BranchDialogRenderer());
    commitComboBox.setMaximumRowCount(6);
    try {
      branches = gitData.getBranches();
    } catch (GitException e) {
      e.printStackTrace();
    }
    for (int i = 0; i < branches.size(); i++){
      branchComboBox.addItem(branches.get(i).getName());
    }


    branchComboBox.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = branchComboBox.getSelectedIndex();
        Iterator<GitCommit> it = null;
        GitBranch actualBranch = branches.get(index);
        commits.clear();
        commitComboBox.removeAllItems();
        try {
          it = actualBranch.getCommits();
        } catch (GitException gitException) {
          gitException.printStackTrace();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
        int count = 0;
        while (it.hasNext()){
          commits.add(count, it.next());
          count++;
        }
        for (int i = 0; i < commits.size(); i++){
          commitComboBox.addItem(commits.get(i).getMessage());
        }
      }
    });
    branchButton.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        if (commitComboBox.getSelectedItem() == null){
          GUIController.getInstance().errorHandler("Es muss ein Commit ausgewählt werden. Beim Auswählen eines" +
                  "Branches wird standartmäßig der letzte Commit ausgewählt");
          return;
        }
        GitCommit commitForOp = commits.get(commitComboBox.getSelectedIndex());
        String nameOfBranch = nameField.getText();
        if (nameOfBranch.compareTo("") == 0){
          GUIController.getInstance().errorHandler("Es muss ein Name eingegeben werden");
          return;
        }
        Branch branch = new Branch();
        branch.setBranchPoint(commitForOp);
        branch.setBranchName(nameOfBranch);
        branch.execute();
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
    return "Branch";
  }

  /**
   * The Size of the newly created Dialog
   *
   * @return 2D Dimension
   */
  @Override
  public Dimension getDimension() {
    return new Dimension(800,300);
  }

  /**
   * The content Panel containing all contents of the Dialog
   *
   * @return the shown content
   */
  @Override
  public JPanel getPanel() {
    return BranchPanel;
  }

  public void update() {

  }
  private static class BranchDialogRenderer extends JTextArea implements ListCellRenderer {
    private int minRows;

    public BranchDialogRenderer() {
      this.setLineWrap(true);
      this.setWrapStyleWord(true);
    }

    @Override
    public Component getListCellRendererComponent(final JList list,
                                                  final Object value, final int index, final boolean isSelected,
                                                  final boolean hasFocus) {
      Color background = Color.WHITE;
      this.setText((String) value);
      // Only the first 6 lines of the commit message should be shown;
      //this.setRows(minRows);
      int width = list.getWidth();
      if(isSelected) {
        // This color is light blue.
        background = new Color(0xAAD8E6);
      }
      this.setBackground(background);
      // this is just to activate the JTextAreas internal sizing mechanism
      //if (width > 0) {
        this.setSize(width, Short.MAX_VALUE);
      //}
      this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      return this;

    }
  }
}