package dialogviews;

import controller.GUIController;
import git.CredentialProviderHolder;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog to ask for the user's credentials when executing a
 * remote operation.
 */
public class UsernamePasswordDialogView implements IDialogView {
    @SuppressWarnings("unused")
    private JPanel panel1;
    private JPanel userPwPanel;
    private JTextField textField1;
    private JPasswordField passwordField;
    @SuppressWarnings("unused")
    private JLabel userNameLabel;
    private JButton okButton;
    @SuppressWarnings("unused")
    private JLabel pwLabel;
    private JButton breakButton;
    private final String name;


    @Override
    public String getTitle() {
        return "Benutzername - " + name;
    }

    /**
     * New Dialog
     * @param name Shown in dialog title to identify the credential usage
     */
    public UsernamePasswordDialogView(String name) {
        this.name = name;
        okButton.addActionListener(e -> {
            String username = textField1.getText();
            char[] password = passwordField.getPassword();
            StringBuilder pw = new StringBuilder();
            for (char c : password) {
                pw.append(c);
            }
            // Configures the Variables of the ProviderHolder
            CredentialProviderHolder.getInstance().setPassword(pw.toString());
            CredentialProviderHolder.getInstance().setUsername(username);
            CredentialProviderHolder.getInstance().setActive(true);
            GUIController.getInstance().closeDialogView();
        });
        breakButton.addActionListener(e -> {
            CredentialProviderHolder.getInstance().setActive(false);
            GUIController.getInstance().closeDialogView();
        });
    }

    @Override
    public JPanel getPanel() {
        return userPwPanel;
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(400, 200);
    }

    @Override
    public void update() {
        // Is not needed for this dialogview
    }
}
