package views.listCellRenderer;

import javax.swing.*;
import java.awt.*;

public class HistoryViewRenderer extends JTextArea implements ListCellRenderer {
  private int minRows;

  /**
   * Sets the minimal amount of rows required by one list entry. A list entry has to be a String
   * in order to work properly. If the item size should dynamically grow set minRows = 1.
   * The JList which wants to dynamically grow needs to invoke a componentListener.
   * For an example how to use the componentListener look in the class HistoryView.
   * @param minRows the minimal count of rows contained in one ListCell.
   */
  public HistoryViewRenderer(int minRows) {
    this.minRows = minRows;
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
    this.setRows(minRows);
    int width = list.getWidth();
    if(isSelected) {
      background = Color.BLUE;
    }
    this.setBackground(background);
    // this is just to activate the JTextAreas internal sizing mechanism
    if (width > 0) {
      this.setSize(width, Short.MAX_VALUE);
    }
    this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    return this;

  }
}
