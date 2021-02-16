package dialogviews;

import commands.Config;
import commands.Init;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FirstUseDialogView implements IDialogView {
  private JFrame frame = new JFrame("FirstUseDialogView");
  private JTextField nameField;
  private JTextField eMailField;
  private JButton chooseButton;
  private JButton finishButton;
  private JPanel FirstUseDialog;
  private JLabel errorLabel;
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
          errorLabel.setText(config.getErrorMessage());
          return;
        }

         */
        init = new Init();
        init.setPathToRepository(path);
        boolean successInit = init.execute();
        if(!successInit) {
          errorLabel.setText(init.getErrorMessage());
          return;
        }
        frame.setVisible(false);
      }
    });
    // Opens a new JFileChooser to set a path to a directory.
    chooseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(FirstUseDialog);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
          path = chooser.getSelectedFile();
        }
      }
    });
  }

/*  public void show() {
    frame.setContentPane(new FirstUseDialogView().FirstUseDialog);
    JPanel panel = new JPanel();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setSize(700, 300);
    frame.setVisible(true);
  }*/

  /**
   * DialogWindow Title
   *
   * @return Window Title as String
   */
  @Override
  public String getTitle() {
    return null;
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
