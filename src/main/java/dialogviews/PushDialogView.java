package dialogviews;

import javax.swing.*;
import java.awt.*;

public class PushDialogView implements IDialogView {

  private JPanel contentPane;
  private JComboBox remoteComboBox;
  private JComboBox remoteBranchComboBox;
  private JComboBox localBranchComboBox;
  private JCheckBox setUpstreamCheckbox;
  private JButton aktualisierenButton;
  private JButton pushButton;
  private JPanel remotePanel;

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
    return null;
  }

  public void update() {

  }

}
