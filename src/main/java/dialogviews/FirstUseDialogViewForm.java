package dialogviews;

import javax.swing.*;

public class FirstUseDialogViewForm {
    private JTextField textField1;
    private JTextField textField2;

    public void open() {
        JFrame frame = new JFrame("FirstUseDialogViewForm");
        frame.setContentPane(new FirstUseDialogViewForm().FirstUseDialog);
        JPanel panel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public JPanel returnPanel() {
        return new FirstUseDialogViewForm().FirstUseDialog;
    }

    private JButton auswählenButton;
    private JButton fertigButton;
    private JPanel FirstUseDialog;

    public void setData(FirstUseDialogViewForm data) {

    }

    public void getData(FirstUseDialogViewForm data) {
    }

    public boolean isModified(FirstUseDialogViewForm data) {
        return false;
    }


}
