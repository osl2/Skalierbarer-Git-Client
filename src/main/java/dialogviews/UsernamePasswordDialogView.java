package dialogviews;

import controller.GUIController;
import git.CredentialProviderHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UsernamePasswordDialogView implements IDialogView {
    @SuppressWarnings("unused")
    private JPanel panel1;
    private JPanel UserPwPanel;
    private JTextField textField1;
    private JPasswordField passwordField;
    @SuppressWarnings("unused")
    private JLabel userNameLabel;
    private JButton okButton;
    @SuppressWarnings("unused")
    private JLabel pwLabel;
    private JButton breakButton;
    private final String name;

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Benutzername - " + name;
    }

    public UsernamePasswordDialogView(String name) {
        this.name = name;
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
                StringBuilder pw = new StringBuilder();
                for (char c : password) {
                    pw.append(c);
                }
                // Configures the Variables of the ProviderHolder
                CredentialProviderHolder.getInstance().setPassword(pw.toString());
                CredentialProviderHolder.getInstance().setUsername(username);
                CredentialProviderHolder.getInstance().setActive(true);
                GUIController.getInstance().closeDialogView();
            }
        });
        breakButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                CredentialProviderHolder.getInstance().setActive(false);
                GUIController.getInstance().closeDialogView();
            }
        });
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
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(400, 200);
    }

    /**
     * Refresh the contents of the Dialog window,
     * i.e. when data changes
     */
    @Override
    public void update() {

    }
}
