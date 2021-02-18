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
  private JButton deleteButton;
  private JPanel buttonPanel2;
  private List<GitRemote> remotes;
  private List<GitBranch> branches;
  private Remote remForSetURL = new Remote();
  private Remote remForSetName = new Remote();


  public RemoteView(){
    remForSetURL.setRemoteSubcommand(Remote.RemoteSubcommand.INACTIVE);
    remForSetName.setRemoteSubcommand(Remote.RemoteSubcommand.INACTIVE);
    GitData git = new GitData();
    remotes = git.getRemotes();
    DefaultListModel<GitRemote> model = new DefaultListModel<GitRemote>();
    for (int i = 0; i < remotes.size(); i++){
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
        remForSetName.setRemote(remotes.get(index));
        if (nameField.getText().compareTo("") == 0){
          GUIController.getInstance().errorHandler("Kein name eingegeben");
          return;
        }
        remForSetName.setRemoteName(nameField.getText());
        try {
          remForSetName.setUrl(new URL(urlField.getText()));
        } catch (MalformedURLException malformedURLException) {
          GUIController.getInstance().errorHandler("Keine gültige Url");
          return;
        }
        remForSetURL.setRemote(remotes.get(index));
        try {
          URL url = new URL(urlField.getText());
          remForSetURL.setUrl(url);
        } catch (MalformedURLException malformedURLException) {
          GUIController.getInstance().errorHandler("Diese URL ist nicht gültig");
          return;
        }
          if (remForSetURL.execute()){
            remotes = git.getRemotes();
            DefaultListModel<GitRemote> newModel = new DefaultListModel<GitRemote>();
            for (int i = 0; i < remotes.size(); i++){
              newModel.add(i, remotes.get(i));

            }
            remoteList.setModel(newModel);
            remoteList.setSelectedIndex(index);
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
          remForSetURL.setRemoteSubcommand(Remote.RemoteSubcommand.INACTIVE);

        if (remForSetName.execute()){
          remotes = git.getRemotes();
          DefaultListModel<GitRemote> newModel = new DefaultListModel<GitRemote>();
          for (int i = 0; i < remotes.size(); i++){
            newModel.add(i, remotes.get(i));
          }
          remoteList.setModel(newModel);
          nameField.setText("");
          urlField.setText("");
          branchArea.setText("");

        }
        remForSetName.setRemoteSubcommand(Remote.RemoteSubcommand.INACTIVE);
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

    urlField.addFocusListener(new FocusAdapter() {
      /**
       * Invoked when a component gains the keyboard focus.
       *
       * @param e
       */
      @Override
      public void focusGained(FocusEvent e) {
        if (remForSetName.getRemoteSubcommand() == Remote.RemoteSubcommand.INACTIVE){
          remForSetURL.setRemoteSubcommand(Remote.RemoteSubcommand.SET_URL);
        }
      }
    });
    nameField.addFocusListener(new FocusAdapter() {
      /**
       * Invoked when a component gains the keyboard focus.
       *
       * @param e
       */
      @Override
      public void focusGained(FocusEvent e) {
        remForSetName.setRemoteSubcommand(Remote.RemoteSubcommand.SET_NAME);
        remForSetURL.setRemoteSubcommand(Remote.RemoteSubcommand.INACTIVE);
      }
    });
    deleteButton.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = remoteList.getSelectedIndex();
        if (index < 0){
          GUIController.getInstance().errorHandler("Es ist kein Remote ausgewählt");
          return;
        }
        Remote retRemote = new Remote();
        retRemote.setRemoteSubcommand(Remote.RemoteSubcommand.REMOVE);
        retRemote.setRemote(remotes.get(index));
        if (retRemote.execute()){
          remotes = git.getRemotes();
          DefaultListModel<GitRemote> model = new DefaultListModel<GitRemote>();
          for (int i = 0; i < remotes.size(); i++){
            model.add(i, remotes.get(i) );
          }
          remoteList.setModel(model);
          nameField.setText("");
          urlField.setText("");
          branchArea.setText("");
        }
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