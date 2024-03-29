/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
import java.util.List;

/**
 * Shows an overview about all configured remotes and allows to change their settings
 */
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
  private String actualUrl;

  /**
   * Constructor to create RemoteView
   */
  public RemoteView() {
    testGUI();
    //Set the subcommand to set the Url to inactive
    remForSetURL.setRemoteSubcommand(Remote.RemoteSubcommand.INACTIVE);
    GitData git = new GitData();
   loadRemotes();
   //ActtionListener to go back to the Mainwindow
    removeButton.addActionListener(e -> GUIController.getInstance().restoreDefaultView());
    safeButton.addActionListener(e -> actionSafe()
    );
    addButton.addActionListener(e -> GUIController.getInstance().openDialog(new RemoteAddDialogView()));
    deleteButton.addActionListener(e -> {
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
        DefaultListModel<GitRemote> model1 = new DefaultListModel<>();
        for (int i = 0; i < remotes.size(); i++) {
          model1.add(i, remotes.get(i));
        }
        remoteList.setModel(model1);
        nameField.setText("");
        urlField.setText("");
        branchArea.setText("");
      }
    });
    remoteList.addListSelectionListener(e -> {
      int index = remoteList.getSelectedIndex();
      if (index < 0){
        return;
      }
      GitRemote act = remotes.get(index);
      nameField.setText(act.getName());
      urlField.setText(act.getUrl());
      actualUrl = urlField.getText();
      reloadBranches();
    });
  }

  @Override
  public void update() {
    loadRemotes();
  }

  /**
   * Method to get the RemotePanel
   *
   * @return RemotePnael for the mainwindow
   */
  @Override
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
      GUIController.getInstance().errorHandler("Die branches konnten nicht geladen werden.");
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
     * value. That component's {@code paint} method is then called to
     * "render" the cell.  If it is necessary to compute the dimensions
     * of a list because the list cells do not have a fixed size, this method
     * is called to generate a component on which {@code getPreferredSize}
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
  private void loadRemotes(){
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

  }
  private void actionSafe(){
    int index = remoteList.getSelectedIndex();
    // If no remote is selected show an error-Message
    if (index < 0) {
      GUIController.getInstance().errorHandler("Es muss ein remote ausgewählt werden");
      return;
    }
    if (actualUrl.compareTo(urlField.getText()) != 0){
      remForSetURL.setRemoteSubcommand(Remote.RemoteSubcommand.SET_URL);
    }
    remForSetURL.setRemote(remotes.get(index));
    remForSetURL.setUrl(urlField.getText());
    if (remForSetURL.execute()) {
      GUIController.getInstance().setCommandLine(remForSetURL.getCommandLine());
      ((DefaultListModel<GitRemote>) remoteList.getModel()).set(index, remotes.get(index));
      remoteList.setSelectedIndex(index);
      reloadBranches();
      remoteList.requestFocus();
    }
    remForSetURL.setRemoteSubcommand(Remote.RemoteSubcommand.INACTIVE);
  }
  private void testGUI(){
    nameField.setName("nameField");
    urlField.setName("urlField");
    remoteList.setName("remoteList");
    addButton.setName("addButton");
    removeButton.setName("removeButton");
    safeButton.setName("safeButton");
    deleteButton.setName("deleteButton");
    branchArea.setName("branchArea");
  }
}
