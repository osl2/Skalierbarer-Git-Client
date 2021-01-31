package controller;

import dialogviews.IDialogView;
import views.HistoryView;
import views.IView;
import views.MainWindow;

public class GUIController {

    private static GUIController INSTANCE;
    private MainWindow window;

    private GUIController() {
        /* This class is a singleton */
    }

    public static GUIController getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GUIController();

        return INSTANCE;
    }

    /**
     * Set the instance to be managed by the controller
     *
     * @param window the instance to be managed
     */
    public void setWindow(MainWindow window) {
        this.window = window;
    }

    /**
     * This method can be used to unload a {@link IView} which was loaded with {@link #openView(IView)}.
     * Currently this method restores an instance of {@link HistoryView}
     */
    public void restoreDefaultView() {
        IView defaultView = new HistoryView();
        this.openView(defaultView);
    }

    /**
     * Close an open Dialog.
     *
     * @param dialog the Dialog to be closed
     */
    public void closeDialogView(IDialogView dialog) {
        // Do we need custom modal logic? Will our dialogs just give us a JPanel?


    }

    /**
     * Replace the commandLine text in the MainWindow
     *
     * @param commandLine the new text to be shown
     */
    public void setCommandLine(String commandLine) {

    }

    /**
     * Open a Dialog
     *
     * @param dialog the Dialog to be opened
     */
    public void openDialog(IDialogView dialog) {
        /* TODO: Resolve: The easiest way to resolve the modality issue would be to construct the JDialog here
                          For that we need more stuff (Title, Size, etc.) in the IDialogView interface */
    }

    /**
     * Change the view in the MainWindow
     *
     * @param view the View to be opened
     */
    public void openView(IView view) {
        // TODO: Resolve: This should not always be possible. What logic do we need to add here?
        this.window.setView(view);
    }


    // TODO: Resolve: Why do we have this split? Is it even necessary? Would a composition pattern make more sense here?
    public void update(IView view) {
        throw new AssertionError("not implemented");
    }

    public void update(IDialogView dialog) {
        throw new AssertionError("not implemented");
    }

    /**
     * Show the MainWindow.
     */
    public void openMainWindow() {
        this.window.open();
    }
}
