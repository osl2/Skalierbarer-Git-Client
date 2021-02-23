package dialogviews;

import commands.Remote;
import controller.GUIController;
import views.RemoteView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RemoteAddDialogView implements IDialogView {
    private JPanel panel1;
    private JPanel remoteAddPanel;
    private JTextField namefield;
    private JTextField urlField;
    private JButton stopButton;
    private JButton addButton;
    private JLabel nameLabel;
    private JLabel urlLabel;

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Remote hinzufügen";
    }

    public RemoteAddDialogView() {
        stopButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                GUIController.getInstance().closeDialogView();
            }
        });
        addButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = namefield.getText();
                if (name.compareTo("") == 0) {
                    GUIController.getInstance().errorHandler("Kein Name eingegeben");
                    return;
                }
                Remote rem = new Remote();
                rem.setRemoteSubcommand(Remote.RemoteSubcommand.ADD);
                rem.setRemoteName(name);
                rem.setUrl(urlField.getText());
                if (rem.execute()) {
                    GUIController.getInstance().setCommandLine(rem.getCommandLine());
                    GUIController.getInstance().closeDialogView();
                    GUIController.getInstance().openView(new RemoteView());
                }
            }
        });
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(300, 150);
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return remoteAddPanel;
    }

    /**
     * Refresh the contents of the Dialog window,
     * i.e. when data changes
     */
    @Override
    public void update() {

    }
}
