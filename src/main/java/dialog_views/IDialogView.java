package dialog_views;

public interface IDialogView {

  /**
   * Open (and create if necessary) the Dialog
   */
  void show();

  /**
   * Refresh the contents of the Dialog window,
   * i.e. when data changes
   */
  void update();

}