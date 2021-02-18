package dialogviews;

import commands.Remote;
import controller.GUIController;
import views.RemoteView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

public class RemoteAddDialogView implements IDialogView{
    private JPanel panel1;
    private JPanel remoteAddPanel;
    private JTextField namefield;
    private JTextField urlField;
    private JButton stopButton;
    private JButton addButton;
    private JLabel nameLabel;
    private JLabel urlLabel;

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
                if (name.compareTo("") == 0){
                    GUIController.getInstance().errorHandler("Kein name eingegeben");
                    return;
                }
                URL url = null;
                try {
                    url = new URL(urlField.getText());
                } catch (MalformedURLException malformedURLException) {
                    GUIController.getInstance().errorHandler("Keine gültige URL eingegeben");
                    return;
                }
                Remote rem = new Remote();
                rem.setRemoteSubcommand(Remote.RemoteSubcommand.ADD);
                rem.setRemoteName(name);
                rem.setUrl(url);
                if (rem.execute()){
                    GUIController.getInstance().setCommandLine(rem.getCommandLine());
                    GUIController.getInstance().closeDialogView();
                    GUIController.getInstance().openView(new RemoteView());
                }
            }
        });
    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Remote hinzufügen";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(300,150);
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
