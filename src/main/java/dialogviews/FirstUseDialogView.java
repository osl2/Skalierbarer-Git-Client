package dialogviews;

import commands.Config;
import commands.Init;
import controller.GUIController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FirstUseDialogView implements IDialogView {
  private JTextField nameField;
  private JTextField eMailField;
  private JButton chooseButton;
  private JButton finishButton;
  private JPanel firstUseDialog;
  private String name = null;
  private String eMail = null;
  private File path = null;
  private Init init;
  private Config config;
  private JFileChooser chooser;



  public FirstUseDialogView() {
    finishButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        name = nameField.getText();
        eMail = eMailField.getText();
        config = new Config();
        config.setName(name);
        config.setEMail(eMail);
        boolean successConfig = config.execute();
        /*
        TODO: Wird nicht benötigt, da execute() selbst den error handler vom GUI Controller aufruft, oder?

        if(!successConfig) {
          return;
        }

         */
        init = new Init();
        init.setPathToRepository(path);
        boolean successInit = init.execute();
        if(!successInit) {
          return;
        }
        GUIController.getInstance().closeDialogView();
      }
    });
    // Opens a new JFileChooser to set a path to a directory.
    chooseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
          path = chooser.getSelectedFile();
        }
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
    return "Erstbenutzung";
  }

  /**
   * The Size of the newly created Dialog
   *
   * @return 2D Dimension
   */
  @Override
  public Dimension getDimension() {
    Dimension dim = new Dimension(700, 300);
    return dim;
  }

  /**
   * The content Panel containing all contents of the Dialog
   *
   * @return the shown content
   */
  @Override
  public JPanel getPanel() {
    return firstUseDialog;
  }

  public void update() {

  }

}
