package dialogviews;

import javax.swing.*;
import java.awt.*;

public class FetchDialogView implements IDialogView {

  private JPanel FetchPanel;
  private JScrollPane treeScrollPanel;
  private JTree fetchTree;
  private JPanel bottomPanel;
  private JButton fetchButton;

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
