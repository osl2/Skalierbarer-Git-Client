package controller;

import dialogviews.IDialogView;
import views.IView;

public class GUIController {

    private static GUIController INSTANCE;

    private GUIController() {

    }

    public static GUIController getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GUIController();

        return INSTANCE;
    }

    /**
     * This method can be used to unload a IView which was loaded with setView.
     * Currently this method restores an instance of HistoryView
     * TODO: JavaDoc: Tags
     */
    public void restoreDefaultView() {

    }

    public void closeDialogView(IDialogView dialog) {

    }

    public void setCommandLine(String commandLine) {

    }

    public void openDialog(IDialogView dialog) {

    }

    public void openView(IView view) {
    }

    public void update(IView view) {
    }

    public void update(IDialogView dialog) {
    }
}
