package dialogviews;

import javax.swing.*;
import java.awt.*;

public class UsernamePasswordDialogView implements IDialogView{

    private JPanel panel1;
    private JPanel UserPwPanel;
    private JTextField textField1;
    private JPasswordField passwordField;
    private JLabel userNameLabel;
    private JButton okButton;
    private JLabel pwLabel;

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
