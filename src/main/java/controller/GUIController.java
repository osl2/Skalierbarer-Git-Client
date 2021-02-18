package controller;

import com.formdev.flatlaf.FlatLightLaf;
import commands.ICommandGUI;
import dialogviews.IDialogView;
import settings.Settings;
import views.HistoryView;
import views.IView;
import views.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;

public class GUIController {

    private static GUIController INSTANCE;
    private MainWindow window;
    private JDialog currentDialog;
    private IDialogView currentDialogAnchor;

    private GUIController() {
        /* This class is a singleton */

        // Install FlatLaf light style
        FlatLightLaf.install();

    }

    public static GUIController getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GUIController();

        return INSTANCE;
    }

    /**
     * Default handler for any Exception which could not be handled by the program, but does not force
     * us to exit.
     *
     * @param e The unhandled exception
     */
    public void errorHandler(Exception e) {
        this.errorHandler(e.getMessage());
    }


    /**
     * See {@link #errorHandler(Exception)}. Also used to show an error dialog with a custom text to the user
     *
     * @param msg Text of the error dialog
     */
    public void errorHandler(String msg) {
        JTextArea jTextArea = new JTextArea(msg);
        JPanel bufferPanel = new JPanel(new BorderLayout());
        bufferPanel.add(new JScrollPane(jTextArea));
        jTextArea.setEditable(false);
        if (jTextArea.getPreferredSize().height > 600 || jTextArea.getPreferredSize().width > 600)
            bufferPanel.setPreferredSize(new Dimension(800, 600));
        else
            bufferPanel.setPreferredSize(jTextArea.getPreferredSize());
        bufferPanel.revalidate();
        JOptionPane.showMessageDialog(this.currentDialog,
                bufferPanel,
                "Fehler",
                JOptionPane.ERROR_MESSAGE);

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
        this.currentDialogAnchor = null;
        this.update();
    }

    /**
     * Replace the commandLine text in the MainWindow
     *
     * @param commandLine the new text to be shown
     */
    public void setCommandLine(String commandLine) {
        if (commandLine != null)
            this.window.setCommandLineText(commandLine);
    }

    public void initializeMainWindow() {
        this.window = new MainWindow();

        for (ICommandGUI c : Settings.getInstance().getLevel().getCommands()) {
            if (c.getName() == null || c.getDescription() == null) {
                Logger.getGlobal().warning(c.getClass().getCanonicalName() + " not loaded because it returned null values");
                continue;
            }
            this.window.addButton(c.getName(), c.getDescription(), e -> c.onButtonClicked());
        }
    }

    /**
     * Open a Dialog
     *
     * @param dialog   the Dialog to be opened
     * @param listener WindowListener to attach to the dialog
     */
    public void openDialog(IDialogView dialog, WindowListener listener) {
        this.currentDialog = createDialog(dialog);
        this.currentDialogAnchor = dialog;
        if (listener != null)
            currentDialog.addWindowListener(listener);

        currentDialog.setVisible(true);
    }

    /**
     * See {@link #openDialog(IDialogView, WindowListener)}
     */
    public void openDialog(IDialogView dialog) {
        this.openDialog(dialog, null);
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
        this.window.setView(view);
        this.update();
    }


    public void update() {
        if (this.currentDialogAnchor != null) {
            this.currentDialogAnchor.update();
        }
        if (this.window != null)
            this.window.update();
    }

    /**
     * Show the MainWindow.
     */
    public void openMainWindow() {
        this.window.open();
    }
}
