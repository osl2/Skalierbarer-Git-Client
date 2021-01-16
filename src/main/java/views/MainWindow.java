package views;

public class MainWindow {
  /* The MainWindow is NOT a view, rather a container for views, and therefore should NOT implement IView! */
  /* TODO: This class should probably extend JFrame or the equivalent in JavaFX */

  private IView loadedView;

  public boolean setView(IView view) {
    this.loadedView = view;
    /* Set View */
    return false;
  }

  /**
   * This method can be used to unload a IView which was loaded with setView.
   * Currently this method restores an instance of HistoryView
   * TODO: JavaDoc: Tags
   */
  public void restoreDefaultView() {

  }
}
