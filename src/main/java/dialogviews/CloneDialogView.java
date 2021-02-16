package dialogviews;

import commands.Clone;
import controller.GUIController;
import git.CredentialProviderHolder;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
  private boolean needNew = false;

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
    chooseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(cloneDialog);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
          path = chooser.getSelectedFile();
        }
      }
    });
    cloneButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clone = new Clone();
        clone.setGitURL(remoteField.getText());
        clone.cloneRecursive(recursiveCheckBox.isSelected());
        clone.setDestination(path);
        boolean success = false;
        success = clone.execute();
        if(success) {
          GUIController.getInstance().setCommandLine(clone.getCommandLine());
          GUIController.getInstance().closeDialogView();
        }
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
    return "Clone";
  }

  /**
   * The Size of the newly created Dialog
   *
   * @return 2D Dimension
   */
  @Override
  public Dimension getDimension() {
    Dimension dim = new Dimension(500, 400);
    return dim;
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

  public boolean getNeedNew() {
    return needNew;
  }
}
