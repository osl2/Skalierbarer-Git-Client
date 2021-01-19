package views;

import views.filter.AbstractHistoryFilter;

import javax.swing.JPanel;

public class HistoryView extends JPanel implements IView {
  public JPanel getView() {
    return this;
  }

  private void applyFilter(AbstractHistoryFilter filter) {
  }
}
