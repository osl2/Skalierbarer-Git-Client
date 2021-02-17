package controller;

import com.formdev.flatlaf.FlatLightLaf;
import dialogviews.IDialogView;
import views.HistoryView;
import views.IView;
import views.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class GUIController {

    private static GUIController INSTANCE;
    private MainWindow window;
    private JDialog currentDialog;
    private IDialogView currentDialogAnchor;

    private GUIController() {
        /* This class is a singleton */

        // Install FlatLaf light style
        FlatLightLaf.install();

        // Setup MainWindow
        initializeMainWindow();
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

    private void initializeMainWindow() {
        this.window = new MainWindow();
        for (int i = 1; i <= 10; i++) {
            int finalI = i;
            this.window.addButton("Button " + i, "Tooltip" + i,
                    e -> System.out.printf("Button %d gedrückt%n", finalI)
            );
        }
    }

    /**
     * Open a Dialog
     *
     * @param dialog the Dialog to be opened
     */
    public void openDialog(IDialogView dialog) {
        this.currentDialog = createDialog(dialog);
        this.currentDialogAnchor = dialog;
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
        this.window.setView(view);
    }


    public void update() {
        if (this.currentDialogAnchor != null) {
            this.currentDialogAnchor.update();
        }
    }

    /**
     * Show the MainWindow.
     */
    public void openMainWindow() {
        this.window.open();
    }
}
