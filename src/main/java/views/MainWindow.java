package views;

import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {

    private IView loadedView;
    // Main-Panel
    private JPanel contentPane;
    // Panel containing our button row
    private JPanel buttonPanel;
    // Replacable panel
    private JPanel viewPanel;
    private JPanel headPanel;
    private JTextField commandLineTextField;

    /**
     * Replace the lower view of the window.
     * This will invoke {@link #update()}
     *
     * @param view the IView to show
     * @return true if view was able to be changed
     */
    public boolean setView(IView view) {
        this.loadedView = view;
        // New Data -> Refresh view
        this.update();
        return true;
    }

    public void setCommandLineText(String text) {
        this.commandLineTextField.setText(text);
    }


    /**
     * Get the lower part of the window
     *
     * @return the IView loaded into the lower part of the window
     */
    public IView getView() {
        return this.loadedView;
    }


    /**
     * Refreshes all dynamic parts of the Window. Including the MenuBar and loaded View
     */
    @SuppressWarnings("BoundFieldAssignment")
    public void update() {
        if (this.loadedView != null) {
            this.contentPane.remove(viewPanel);
            this.viewPanel = this.loadedView.getView();
            this.contentPane.add(viewPanel);
            this.viewPanel.revalidate();
            this.viewPanel.repaint();
        }

        // Refresh menubar
        createMenubar();

        // Enable or disable Tooltips
        ToolTipManager.sharedInstance().setEnabled(Settings.getInstance().useTooltips());
    }

    /**
     * Add a new Button to the ButtonRow in the top
     *
     * @param text     The Button Text
     * @param toolTip  The Tooltip to be shown if {@link Settings#useTooltips()} returns true
     * @param listener The ActionListener to be called when the Button is clicked.
     */
    public void addButton(String text, String toolTip, ActionListener listener) {
        JButton newButton = new JButton(text);
        newButton.setToolTipText(toolTip);
        newButton.addActionListener(listener);
        this.buttonPanel.add(newButton);
    }

    /**
     * Open the Window. This invokes {@link #update()}
     */
    public void open() {
        this.update();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(this.contentPane);
        this.setPreferredSize(new Dimension(1280, 720));
        this.pack();
        this.setVisible(true);
    }

    private void createMenubar() {
        JMenuBar bar = new JMenuBar();
        JMenu m1 = new JMenu("Menu 1");
        JMenu m2 = new JMenu("Menu 2");

        bar.add(m1);
        bar.add(m2);
        this.setJMenuBar(bar);
    }
}
