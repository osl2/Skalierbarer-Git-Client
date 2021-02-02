package views.listCellRenderer;

import javax.swing.*;
import java.awt.*;

public class HistoryViewRenderer extends JTextArea implements ListCellRenderer {
  public HistoryViewRenderer() {
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
    this.setRows(6);
    int width = list.getWidth();
    if(isSelected) {
      background = Color.BLUE;
    }
    this.setBackground(background);
    // this is just to activate the JTextAreas internal sizing mechanism
    if (width > 0) {
      this.setSize(width, Short.MAX_VALUE);
    }
    return this;

  }
}
