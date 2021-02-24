package controller;

import com.formdev.flatlaf.FlatLightLaf;
import commands.ICommandGUI;
import dialogviews.IDialogView;
import settings.Data;
import settings.DataObservable;
import settings.DataObserver;
import settings.Settings;
import views.HistoryView;
import views.IView;
import views.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * Manages and updates and opens all graphical user interface windows.
 */
public class GUIController extends DataObserver {

    private static final int ERROR_MESSAGE_WIDTH = 600;
    private static final int ERROR_MESSAGE_HEIGHT = 800;
    private static GUIController INSTANCE;
    private Map<JDialog, IDialogView> dialogMap;
    private Stack<JDialog> dialogStack;
    private MainWindow window;

    private GUIController() {
        /* This class is a singleton */

        // Install FlatLaf light style
        FlatLightLaf.install();


        // register observer
        Data.getInstance().addDataChangedListener(this);
        Settings.getInstance().addDataChangedListener(this);

        dialogMap = new HashMap<>();
        dialogStack = new Stack<>();

    }

    /**
     * Singleton
     *
     * @return the Instance of the controller.
     */
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
        jTextArea.setSize(ERROR_MESSAGE_WIDTH, jTextArea.getPreferredSize().height);

        if (jTextArea.getPreferredSize().height > ERROR_MESSAGE_HEIGHT ||
                jTextArea.getPreferredSize().width > ERROR_MESSAGE_WIDTH) {

            bufferPanel.setPreferredSize(new Dimension(ERROR_MESSAGE_WIDTH, ERROR_MESSAGE_HEIGHT));
        } else {
            bufferPanel.setPreferredSize(jTextArea.getPreferredSize());
        }
        bufferPanel.revalidate();
        Component target;
        if (dialogStack.isEmpty())
            target = this.window;
        else
            target = dialogStack.peek();
        JOptionPane.showMessageDialog(target,
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
        JDialog currentDialog = dialogStack.pop();
        currentDialog.dispatchEvent(new WindowEvent(currentDialog, WindowEvent.WINDOW_CLOSING));
        dialogMap.put(currentDialog, null);
        this.update();
    }

    /**
     * Replace the commandLine text in the MainWindow
     *
     * @param commandLine the new text to be shown
     */
    public void setCommandLine(String commandLine) {
        if (commandLine != null) {
            this.window.setCommandLineText(commandLine);
            update();
        }
    }

    /**
     * Creates the MainWindow.
     * <p>
     * Closes any existing MainWindows and instantiates a new one.
     */
    public void initializeMainWindow() {
        if (this.window != null) {
            this.window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        }

        this.window = new MainWindow();

        createButtonPanel();
    }

    private void createButtonPanel() {
        this.window.clearButtonPanel();
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
        JDialog currentDialog = createDialog(dialog);
        this.dialogStack.push(currentDialog);
        dialogMap.put(currentDialog, dialog);
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


    /**
     * Refreshes the GUI.
     */
    public void update() {
        if (!dialogStack.isEmpty() && dialogMap.get(dialogStack.peek()) != null) {
            dialogMap.get(dialogStack.peek()).update();
        }

        if (this.window != null) {
            this.createButtonPanel();
            this.window.update();
        }
    }

    /**
     * Show the MainWindow.
     */
    public void openMainWindow() {
        this.window.open();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dataChangedListener(DataObservable observable) {
        this.update();
    }
}
