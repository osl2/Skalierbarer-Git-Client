package dialogviews;

import commands.Clone;
import controller.GUIController;

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

  public CloneDialogView() {
    addActionlistener();
  }

  private void addActionlistener() {
    chooseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //TODO: Wait for filechooser in GUIController
      }
    });
    cloneButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clone = new Clone();
        clone.setGitURL(remoteField.getText());
        clone.cloneRecursive(recursiveCheckBox.isSelected());
        clone.setDestination(path);
        boolean success = clone.execute();
        if(!success) {
          String errorMessage = clone.getErrorMessage();
          //GUIController.getInstance().
          //TODO: ErrorDialogView
          return;
        }
        GUIController.getInstance().setCommandLine(clone.getCommandLine(""));
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
}
