package dialogviews;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import controller.GUIController;
import git.CredentialProviderHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UsernamePasswordDialogView implements IDialogView {

    private JPanel panel1;
    private JPanel UserPwPanel;
    private JTextField textField1;
    private JPasswordField passwordField;
    private JLabel userNameLabel;
    private JButton okButton;
    private JLabel pwLabel;
    private JButton breakButton;
    private String name;

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
                String pw = "";
                for (int i = 0; i < password.length; i++) {
                    pw += password[i];
                }
                CredentialProviderHolder.getInstance().setPassword(pw);
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
        Dimension dim = new Dimension(400, 200);
        return dim;
    }

    /**
     * Refresh the contents of the Dialog window,
     * i.e. when data changes
     */
    @Override
    public void update() {

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        UserPwPanel = new JPanel();
        UserPwPanel.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(UserPwPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(500, 400), new Dimension(500, 400), new Dimension(500, 400), 0, false));
        textField1 = new JTextField();
        UserPwPanel.add(textField1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, -1), new Dimension(200, -1), new Dimension(200, -1), 0, false));
        passwordField = new JPasswordField();
        UserPwPanel.add(passwordField, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, -1), new Dimension(200, -1), new Dimension(200, -1), 0, false));
        final Spacer spacer1 = new Spacer();
        UserPwPanel.add(spacer1, new GridConstraints(1, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(25, -1), new Dimension(25, -1), new Dimension(25, -1), 0, false));
        userNameLabel = new JLabel();
        userNameLabel.setText("Nutzername:");
        UserPwPanel.add(userNameLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(75, -1), new Dimension(75, -1), new Dimension(75, -1), 0, false));
        okButton = new JButton();
        okButton.setText("Ok");
        UserPwPanel.add(okButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(50, -1), new Dimension(50, -1), 0, false));
        final Spacer spacer2 = new Spacer();
        UserPwPanel.add(spacer2, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(25, -1), new Dimension(25, -1), new Dimension(25, -1), 0, false));
        pwLabel = new JLabel();
        pwLabel.setText("Passwort:");
        UserPwPanel.add(pwLabel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(75, -1), new Dimension(75, -1), new Dimension(75, -1), 0, false));
        breakButton = new JButton();
        breakButton.setText("Abbrechen");
        UserPwPanel.add(breakButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
