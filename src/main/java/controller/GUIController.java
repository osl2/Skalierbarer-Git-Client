package controller;

import dialogviews.IDialogView;
import views.HistoryView;
import views.IView;
import views.MainWindow;

import javax.swing.*;
import java.awt.event.WindowEvent;

public class GUIController {

    private static GUIController INSTANCE;
    private MainWindow window;
    private JDialog currentDialog;

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
     * Close the open Dialog.
     */
    public void closeDialogView() {
        this.currentDialog.dispatchEvent(new WindowEvent(currentDialog, WindowEvent.WINDOW_CLOSING));
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
        this.currentDialog = createDialog(dialog);
        currentDialog.setVisible(true);
    }

    private JDialog createDialog(IDialogView dialogView) {
        JDialog dialog = new JDialog(window, dialogView.getTitle(), true);
        dialog.setResizable(false);
        dialog.setContentPane(dialogView.getPanel());
        dialog.revalidate();
        dialog.repaint();
        dialog.setSize(dialogView.getDimension());
        dialog.setLocationRelativeTo(window);
        return dialog;
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


    public void update() {
        throw new AssertionError("not implemented");
    }

    /**
     * Show the MainWindow.
     */
    public void openMainWindow() {
        this.window.open();
    }
}
