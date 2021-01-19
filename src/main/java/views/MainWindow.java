package views;

import javax.swing.*;

public class MainWindow implements  IView {
  /* The MainWindow is NOT a view, rather a container for views, and therefore should NOT implement IView! */
  /* TODO: This class should probably extend JFrame or the equivalent in JavaFX */

  private IView loadedView;

  public boolean setView(IView view) {
    this.loadedView = view;
    /* Set View */
    return false;
  }


  /**
   * Returns the lower part of the main window.
   */
  public JPanel getView() {
    throw new AssertionError("not implemented yet");
  }
}
