package views;

import controller.GUIController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RemoteView extends JPanel implements IView {
  private JPanel panel1;
  private JPanel remotePanel;
  private JScrollPane remotePane;
  private JList remoteList;
  private JButton addButton;
  private JPanel buttonPanel;
  private JButton removeButton;
  private JTextField textField1;
  private JTextField textField2;
  private JButton safeButton;
  private JLabel nameLabel;
  private JLabel urlLabel;

  public RemoteView(){

    removeButton.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIController.getInstance().restoreDefaultView();
      }
    });
  }


  public JPanel getView() {
    return remotePanel;
  }

  public void update() {

  }
}