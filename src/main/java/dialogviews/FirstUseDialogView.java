package dialogviews;

import commands.Config;
import commands.Init;
import controller.GUIController;
import settings.Data;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
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
    finishButton.addActionListener(e -> {
      name = nameField.getText();
      eMail = eMailField.getText();
      config = new Config();
      config.setName(name);
      config.setEMail(eMail);
      boolean successConfig = config.execute();

      init = new Init();
      init.setPathToRepository(path);
      boolean successInit = init.execute();
      if (!successInit) {
        return;
      }
      // Set active repository
      Settings.getInstance().setActiveRepositoryPath(path);
      Data.getInstance().storeNewRepositoryPath(path);
      GUIController.getInstance().closeDialogView();
    });
    // Opens a new JFileChooser to set a path to a directory.
    chooseButton.addActionListener(e -> {
      chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int returnVal = chooser.showOpenDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        path = chooser.getSelectedFile();
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
    return new Dimension(500, 150);
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
