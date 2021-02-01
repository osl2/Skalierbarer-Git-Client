package dialogviews;

import commands.Init;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FirstUseDialogView implements IDialogView {
    private JTextField nameField = new JTextField();
    private JTextField eMailField = new JTextField();
    private JButton chooseButton = new JButton();
    private JButton finishButton = new JButton();
    private JPanel FirstUseDialog;
    private String name = null;
    private String eMail = null;
    private File path = null;
    private Init init;
    private JFileChooser chooser;


    public FirstUseDialogView() {
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                name = nameField.getText();
                eMail = eMailField.getText();
                // Only performs any action if all entries are not empty.
                if(name != null && eMail != null && path != null) {
                    init = new Init();
                    init.setPathToRepository(path.getAbsolutePath());
                }
            }
        });
        // Opens a new JFileChooser to set a path to a directory.
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(FirstUseDialog);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    path = chooser.getSelectedFile();
                }
            }
        });
    }

    public void show() {
        JFrame frame = new JFrame("FirstUseDialogView");
        frame.setContentPane(new FirstUseDialogView().FirstUseDialog);
        JPanel panel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void update() {

    }


}
