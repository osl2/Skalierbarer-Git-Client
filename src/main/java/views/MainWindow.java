package views;

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
    // Test-Button im Gui-Designer
    private JButton button1;

    public boolean setView(IView view) {
        this.loadedView = view;
        // New Data -> Refresh view
        this.update();
        return false;
    }


    /**
     * Returns the lower part of the main window.
     */
    public JPanel getView() {
        throw new AssertionError("not implemented yet");
    }

    @SuppressWarnings("BoundFieldAssignment")
    public void update() {
        if (this.loadedView != null)
            this.viewPanel = this.loadedView.getView();

        // Refresh menubar
        createMenubar();
    }

    public void addButton(String text, String toolTip, ActionListener listener) {
        JButton newButton = new JButton(text);
        newButton.setToolTipText(toolTip);
        newButton.addActionListener(listener);
        this.buttonPanel.add(newButton);
    }

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
