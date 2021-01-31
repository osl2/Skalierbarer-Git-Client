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

    public void setWindow(MainWindow window) {
        this.window = window;
    }

    /**
     * This method can be used to unload a IView which was loaded with setView.
     * Currently this method restores an instance of HistoryView
     * TODO: JavaDoc: Tags
     */
    public void restoreDefaultView() {
        IView defaultView = new HistoryView();
        this.openView(defaultView);
    }

    public void closeDialogView(IDialogView dialog) {
        // If we implement the interface changes: This will be enough. Otherwise we need custom modality logic


    }

    public void setCommandLine(String commandLine) {

    }

    public void openDialog(IDialogView dialog) {
        /* TODO: Resolve: The easiest way to resolve the modality issue would be to construct the JDialog here
                          For that we need more stuff (Title, Size, etc.) in the IDialogView interface */
    }

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

    public void openMainWindow() {
        this.window.open();
    }
}
