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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
public class RemoteView extends JPanel implements IView {
  @SuppressWarnings("unused")
  private JPanel panel1;
  private JPanel remotePanel;
  @SuppressWarnings("unused")
  private JScrollPane remotePane;
  private JList<GitRemote> remoteList;
  private JButton removeButton;
  private JTextField nameField;
  private JTextField urlField;
  @SuppressWarnings("unused")
  private JLabel nameLabel;
  @SuppressWarnings("unused")
  private JLabel urlLabel;
  @SuppressWarnings("unused")
  private JPanel buttonPanel;
  private JButton addButton;
  @SuppressWarnings("unused")
  private JLabel branchLabel;
  @SuppressWarnings("unused")
  private JScrollPane branchPane;
  private JTextArea branchArea;
  private JButton safeButton;
  private JButton deleteButton;
  @SuppressWarnings("unused")
  private JPanel buttonPanel2;
  private List<GitRemote> remotes;
  private List<GitBranch> branches;
  private final Remote remForSetURL = new Remote();

  /**
   * Constructor to create RemoteView
   */
  public RemoteView() {
    //Set the subcommand to set the Url to inactive
    remForSetURL.setRemoteSubcommand(Remote.RemoteSubcommand.INACTIVE);
    GitData git = new GitData();
    // Gets the remotes of the repo and shows them in the List
    remotes = git.getRemotes();
    DefaultListModel<GitRemote> model = new DefaultListModel<>();
    for (int i = 0; i < remotes.size(); i++) {
      model.add(i, remotes.get(i));
    }
    remoteList.setCellRenderer(new RemoteViewRenderer());
    remoteList.setModel(model);

   //ActtionListener to go back to the Mainwindow
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
        // If no remote is selected start an error-Message
        if (index < 0) {
          GUIController.getInstance().errorHandler("Es muss ein remote ausgewählt werden");
          return;
        }
        remForSetURL.setRemote(remotes.get(index));
        remForSetURL.setUrl(urlField.getText());
        if (remForSetURL.execute()) {
          GUIController.getInstance().setCommandLine(remForSetURL.getCommandLine());
          remotes = git.getRemotes();
          DefaultListModel<GitRemote> newModel = new DefaultListModel<>();
          for (int i = 0; i < remotes.size(); i++) {
            newModel.add(i, remotes.get(i));

          }
          remoteList.setModel(newModel);
          remoteList.setSelectedIndex(index);
          reloadBranches();

        }
        remForSetURL.setRemoteSubcommand(Remote.RemoteSubcommand.INACTIVE);

      }
    });
    addButton.addActionListener(new ActionListener() {
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
    urlField.addFocusListener(new FocusAdapter() {
      /**
       * {@inheritDoc}
       */
      @Override
      public void focusGained(FocusEvent e) {
        remForSetURL.setRemoteSubcommand(Remote.RemoteSubcommand.SET_URL);
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
        if (index < 0) {
          GUIController.getInstance().errorHandler("Es ist kein Remote ausgewählt");
          return;
        }
        Remote retRemote = new Remote();
        retRemote.setRemoteSubcommand(Remote.RemoteSubcommand.REMOVE);
        retRemote.setRemote(remotes.get(index));
        if (retRemote.execute()) {
          GUIController.getInstance().setCommandLine(retRemote.getCommandLine());
          remotes = git.getRemotes();
          DefaultListModel<GitRemote> model = new DefaultListModel<>();
          for (int i = 0; i < remotes.size(); i++) {
            model.add(i, remotes.get(i));
          }
          remoteList.setModel(model);
          nameField.setText("");
          urlField.setText("");
          branchArea.setText("");
        }
      }
    });
    remoteList.addListSelectionListener(new ListSelectionListener() {
      /**
       * Called whenever the value of the selection changes.
       *
       * @param e the event that characterizes the change.
       */
      @Override
      public void valueChanged(ListSelectionEvent e) {
        int index = remoteList.getSelectedIndex();
        GitRemote act = remotes.get(index);
        nameField.setText(act.getName());
        urlField.setText(act.getUrl());
        if (!tryBranches(act)) {
          GUIController.getInstance().openView(new RemoteView());
          return;
        }
        StringBuilder set = new StringBuilder();
        for (GitBranch branch : branches) {
          set.append(branch.getName()).append(System.lineSeparator());
        }
        branchArea.setText(set.toString());
      }
    });
  }

  public void update() {

  }

  /**
   * Method to grz the RemotePanel
   *
   * @return RemotePnael for the mainwindow
   */
  public JPanel getView() {
    return remotePanel;
  }

  /**
   * Method to get the remoteBranches
   *
   * @param r the remote
   * @return true if it works, recursive if the provider was wrong, false if the UsernameDialogView is closed
   */
  private boolean tryBranches(GitRemote r) {
    GitData git = new GitData();
    try {
      branches = git.getBranches(r);
      return true;
    } catch (GitException e) {
      CredentialProviderHolder.getInstance().changeProvider(true, r.getName());
      if (CredentialProviderHolder.getInstance().isActive()) {
        return tryBranches(r);
      } else {
        CredentialProviderHolder.getInstance().setActive(true);
        return false;
      }
    }
  }

  /**
   * Method to reload the BRanchField
   */
  private void reloadBranches() {
    GitRemote act = remotes.get(remoteList.getSelectedIndex());
    nameField.setText(act.getName());
    urlField.setText(act.getUrl());
    if (!tryBranches(act)) {
      GUIController.getInstance().openView(new RemoteView());
      return;
    }
    StringBuilder set = new StringBuilder();
    for (GitBranch branch : branches) {
      set.append(branch.getName()).append(System.lineSeparator());
    }
    branchArea.setText(set.toString());
  }

  /**
   * Renderer for the remoteList
   */
  private static class RemoteViewRenderer extends JTextArea implements ListCellRenderer<GitRemote> {

    public RemoteViewRenderer() {
      this.setLineWrap(true);
      this.setWrapStyleWord(true);
    }
    /**
     * Return a component that has been configured to display the specified
     * value. That component's <code>paint</code> method is then called to
     * "render" the cell.  If it is necessary to compute the dimensions
     * of a list because the list cells do not have a fixed size, this method
     * is called to generate a component on which <code>getPreferredSize</code>
     * can be invoked.
     *
     * @param list         The JList we're painting.
     * @param value        The value returned by list.getModel().getElementAt(index).
     * @param index        The cells index.
     * @param isSelected   True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return A component whose paint() method will render the specified value.
     * @see JList
     * @see ListSelectionModel
     * @see ListModel
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends GitRemote> list, GitRemote value, int index, boolean isSelected, boolean cellHasFocus) {
      Color background = Color.WHITE;
      String name = value.getName();
      String url = value.getUrl();
      this.setText(name + System.lineSeparator() + System.lineSeparator() + url);
      // Only the first 6 lines of the commit message should be shown;
      int width = list.getWidth();
      if (isSelected) {
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
}