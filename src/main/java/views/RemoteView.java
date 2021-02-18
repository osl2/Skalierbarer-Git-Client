package views;

import commands.Remote;
import controller.GUIController;
import dialogviews.RemoteAddDialogView;
import git.CredentialProviderHolder;
import git.GitBranch;
import git.GitData;
import git.GitRemote;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class RemoteView extends JPanel implements IView {
  private JPanel panel1;
  private JPanel remotePanel;
  private JScrollPane remotePane;
  private JList remoteList;
  private JButton removeButton;
  private JTextField nameField;
  private JTextField urlField;
  private JLabel nameLabel;
  private JLabel urlLabel;
  private JPanel buttonPanel;
  private JButton hinzufügenButton;
  private JLabel branchLabel;
  private JScrollPane branchPane;
  private JTextArea branchArea;
  private JButton safeButton;
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
    safeButton.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = remoteList.getSelectedIndex();
        if (index < 0){
          GUIController.getInstance().errorHandler("Es muss ein remote ausgewählt werden");
          return;
        }
        Remote rem1 = new Remote();
        rem1.setRemote(remotes.get(index));
        rem1.setRemoteSubcommand(Remote.RemoteSubcommand.SET_NAME);
        if (nameField.getText().compareTo("") == 0){
          GUIController.getInstance().errorHandler("Kein name eingegeben");
          return;
        }
        rem1.setRemoteName(nameField.getText());
        try {
          rem1.setUrl(new URL(urlField.getText()));
        } catch (MalformedURLException malformedURLException) {
          GUIController.getInstance().errorHandler("Keine gültige Url");
          return;
        }
        Remote rem2 = new Remote();
        rem2.setRemote(remotes.get(index));
        rem2.setRemoteSubcommand(Remote.RemoteSubcommand.SET_URL);
        try {
          URL url = new URL(urlField.getText());
          rem2.setUrl(url);
          if (rem2.execute()){
            remotes = git.getRemotes();
            DefaultListModel<GitRemote> newModel = new DefaultListModel<GitRemote>();
            for (int i = 0; i < remotes.size(); i++){
              newModel.add(i, remotes.get(i));

            }
            remoteList.setModel(newModel);
            remoteList.setSelectedIndex(index);
            tryBranches(remotes.get(remoteList.getSelectedIndex()));
            String set = "";
            for (int i = 0; i < branches.size(); i++){
              set = set + branches.get(i).getName()+ System.lineSeparator();
            }
            branchArea.setText(set);
          }
        } catch (MalformedURLException malformedURLException) {
          GUIController.getInstance().errorHandler("Diese URL ist nicht gültig");
          return;
        }
        if (rem1.execute()){
          remotes = git.getRemotes();
          DefaultListModel<GitRemote> newModel = new DefaultListModel<GitRemote>();
          for (int i = 0; i < remotes.size(); i++){
            newModel.add(i, remotes.get(i));
          }
          remoteList.setModel(newModel);
          remoteList.setSelectedIndex(index);
        }
      }
    });
    hinzufügenButton.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIController.getInstance().openDialog(new RemoteAddDialogView());
      }
    });
    remoteList.addMouseListener(new MouseAdapter() {
      /**
       * {@inheritDoc}
       *
       * @param e
       */
      @Override
      public void mousePressed(MouseEvent e) {
        int index = remoteList.getSelectedIndex();
        GitRemote act = remotes.get(index);
        nameField.setText(act.getName());
        urlField.setText(act.getUrl().toString());
        tryBranches(act);
        String set = "";
        for (int i = 0; i < branches.size(); i++){
          set = set + branches.get(i).getName()+ System.lineSeparator();
        }
        branchArea.setText(set);
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
  private boolean tryBranches(GitRemote r){
    GitData git = new GitData();
    try {
      branches = git.getBranches(r);
      return true;
    } catch (GitException e) {
      CredentialProviderHolder.getInstance().changeProvider(true);
      return tryBranches(r);
    }
  }
}