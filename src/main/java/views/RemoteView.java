package views;

import controller.GUIController;
import git.GitBranch;
import git.GitData;
import git.GitRemote;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;

public class RemoteView extends JPanel implements IView {
  private JPanel panel1;
  private JPanel remotePanel;
  private JScrollPane remotePane;
  private JList remoteList;
  private JButton removeButton;
  private JTextField nameField;
  private JTextField urlField;
  private JButton safeButton;
  private JLabel nameLabel;
  private JLabel urlLabel;
  private JPanel buttonPanel;
  private JButton hinzufügenButton;
  private JLabel branchLabel;
  private List<GitRemote> remotes;
  private List<GitBranch> branches;

  public RemoteView(){
    GitData git = new GitData();
    remotes = git.getRemotes();
    //GitRemote[] remoteArray = remotes.toArray(new GitRemote[remotes.size()]);
    DefaultListModel<GitRemote> model = new DefaultListModel<GitRemote>();
    for (int i = 0; i < remotes.size(); i++){
      //String added = remotes.get(i).getUrl().toString();
      //String first = "Name: " + remotes.get(i).getName();
      //String newAdded =   System.lineSeparator() + System.lineSeparator() + "Url: " + added;
      model.add(i, remotes.get(i) );
    }
    remoteList.setCellRenderer(new RemoteViewRenderer());
    remoteList.setModel(model);

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

    remoteList.addFocusListener(new FocusAdapter() {
      /**
       * Invoked when a component gains the keyboard focus.
       *
       * @param e
       */
      @Override
      public void focusGained(FocusEvent e) {
        int i = remoteList.getSelectedIndex();
        GitRemote act = remotes.get(i);
        nameField.setText(act.getName());
        urlField.setText(act.getUrl().toString());

      }
    });

  }


  public JPanel getView() {
    return remotePanel;
  }

  public void update() {

  }
  private static class RemoteViewRenderer extends JTextArea implements ListCellRenderer {

    public RemoteViewRenderer() {
      this.setLineWrap(true);
      this.setWrapStyleWord(true);
    }

    @Override
    public Component getListCellRendererComponent(final JList list,
                                                  final Object value, final int index, final boolean isSelected,
                                                  final boolean hasFocus) {
      Color background = Color.WHITE;
      GitRemote act = (GitRemote) value;
      String name = act.getName();
      String url = act.getUrl().toString();
      this.setText( name + System.lineSeparator() + System.lineSeparator() + url);
      // Only the first 6 lines of the commit message should be shown;
      int width = list.getWidth();
      if(isSelected) {
        // This color is light blue.
        background = new Color(0xAAD8E6);
      }
      this.setBackground(background);
      // this is just to activate the JTextAreas internal sizing mechanism
      if (width > 0) {
        this.setSize(width, Short.MAX_VALUE);
      }
      this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      return this;

    }
  }
  private boolean tryBranches(){

  }
}