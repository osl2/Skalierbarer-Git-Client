package dialogviews;

import commands.Remote;
import controller.GUIController;
import views.RemoteView;

import javax.swing.*;
import java.awt.*;

public class RemoteAddDialogView implements IDialogView {
    @SuppressWarnings("unused")
    private JPanel panel1;
    private JPanel remoteAddPanel;
    private JTextField namefield;
    private JTextField urlField;
    private JButton stopButton;
    private JButton addButton;
    @SuppressWarnings("unused")
    private JLabel nameLabel;
    @SuppressWarnings("unused")
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
        testGUI();
        stopButton.addActionListener(e -> GUIController.getInstance().closeDialogView());
        addButton.addActionListener(e -> {
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
        //Not needed for this class
    }

    /**
     * Is for testing the GUI. Do not change this!!!!!!
     */
    private void testGUI(){
        namefield.setName("namefield");
        urlField.setName("urlField");
        addButton.setName("addButton");
        stopButton.setName("stopButton");
    }
}
