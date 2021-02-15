package dialogviews;

import controller.GUIController;
import git.remoteProvider.CredentialProviderHolder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UsernamePasswordDialogView implements IDialogView{

    private JPanel panel1;
    private JPanel UserPwPanel;
    private JTextField textField1;
    private JPasswordField passwordField;
    private JLabel userNameLabel;
    private JButton okButton;
    private JLabel pwLabel;

    public UsernamePasswordDialogView() {
        okButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField1.getText();
                char[] password = passwordField.getPassword();
                String pw = "";
                for (int i = 0; i < password.length; i++){
                    pw += password[i];
                }
                CredentialProviderHolder.getInstance().setProvider(new UsernamePasswordCredentialsProvider(username, pw));
                GUIController.getInstance().closeDialogView();
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
        return "Benutzername";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        Dimension dim = new Dimension(400,200);
        return dim;
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return UserPwPanel;
    }

    /**
     * Refresh the contents of the Dialog window,
     * i.e. when data changes
     */
    @Override
    public void update() {

    }
}
