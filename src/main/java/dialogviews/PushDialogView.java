package dialogviews;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import commands.Push;
import controller.GUIController;
import git.CredentialProviderHolder;
import git.GitBranch;
import git.GitData;
import git.GitRemote;
import git.exception.GitException;

public class PushDialogView implements IDialogView {

  private JPanel contentPane;
  private JComboBox remoteComboBox;
  private JComboBox remoteBranchComboBox;
  private JCheckBox setUpstreamCheckbox;
  private JButton pushButton;
  private JPanel remotePanel;
  private JButton refreshButton;
  private JComboBox localBranchComboBox;
  private GitData gitData;
  private GitBranch localBranch;
  private GitRemote remote;
  private GitBranch remoteBranch;
  private boolean setUpstream;
  private boolean open;
  private List<GitBranch> remoteBranches;
  private List<GitBranch> localBranches;
  private List<GitRemote> remoteList;

  public PushDialogView() {
    gitData = new GitData();
    setUpLocalBranchComboBox();
    setUpRemoteComboBox();

    localBranchComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = localBranchComboBox.getSelectedIndex();
        localBranch = localBranches.get(index);
        //if remote and local branch have been set, setup the remote branch combo box
        if(localBranch != null && remote != null){
          setUpRemoteBranchComboBox();
        }
      }
    });
    remoteComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = remoteComboBox.getSelectedIndex();
        remote = remoteList.get(index);
        //if remote and local branch have been set, setup the remote branch combo box
        if(remote != null){
          setUpRemoteBranchComboBox();
        }
      }
    });
    pushButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //try to execute push. If successful, close the push dialog view
        if(executePush()){
          GUIController.getInstance().closeDialogView();
        }
      }
    });

    //refresh the list of remotes and remote branches. List of remote branches is refreshed every time a new remote is
    //selected
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setUpRemoteComboBox();
      }
    });
  }

  /**
   * DialogWindow Title
   *
   * @return Window Title as String
   */
  @Override
  public String getTitle() {
    return "Push";
  }

  /**
   * The Size of the newly created Dialog
   *
   * @return 2D Dimension
   */
  @Override
  public Dimension getDimension() {
    return this.contentPane.getPreferredSize();
  }

  /**
   * The content Panel containing all contents of the Dialog
   *
   * @return the shown content
   */
  @Override
  public JPanel getPanel() {
    return this.contentPane;
  }

  public void update() {
    //TODO
  }

    private void createUIComponents() {
      localBranchComboBox = new JComboBox<>();
      remoteComboBox = new JComboBox();
      remoteBranchComboBox = new JComboBox();
  }

  public boolean isOpen() {
    return open;
  }

  private class BranchComboBoxRenderer extends JTextField implements ListCellRenderer<GitBranch> {


    @Override
    public Component getListCellRendererComponent(JList<? extends GitBranch> list, GitBranch value, int index, boolean isSelected, boolean cellHasFocus) {
      GitBranch branch = (GitBranch) value;
      this.setText(branch.getName());
      return this;
    }
  }

  private class RemoteComboBoxRenderer extends JTextField implements ListCellRenderer<GitRemote>{

    @Override
    public Component getListCellRendererComponent(JList<? extends GitRemote> list, GitRemote value, int index, boolean isSelected, boolean cellHasFocus) {
      GitRemote remote = (GitRemote) value;
      this.setText(remote.getName());
      return this;
    }
  }

  /*
  setup the combo box that contains the local branches. Preselects the branch that is currently checked out
   */
  private void setUpLocalBranchComboBox(){
    //localBranchComboBox.setRenderer(new BranchComboBoxRenderer());

    try {
      localBranches = gitData.getBranches();
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e);
      return;
    }
    GitBranch selectedBranch = null;
    try {
      selectedBranch = gitData.getSelectedBranch();
    } catch (IOException e) {
      GUIController.getInstance().errorHandler(e);
      return;
    }
    int count = 0;
    for (GitBranch branch : localBranches){

      localBranchComboBox.addItem(branch.getName());
      if (branch.getName().compareTo((selectedBranch.getName())) == 0){
        localBranchComboBox.setSelectedIndex(count);
      }
      count++;
    }

  }

  /*
  sets ups the combo box that contains the registered remotes. Preselects origin (if existing)
   */
  private void setUpRemoteComboBox(){
    remoteList = gitData.getRemotes();
    int count = 0;
    for (GitRemote remote : remoteList){
      remoteComboBox.addItem(remote.getName());
      if (remote.getName().compareTo("origin") == 0){
        remoteComboBox.setSelectedIndex(count);
      }
      count++;
    }
  }

  private void setUpRemoteBranchComboBox(){
    if (remoteBranches != null){
      remoteBranches.clear();
      remoteBranchComboBox.removeAllItems();
    }

    if(reloadBranches(remote) == false){
      return;
    }

    if (remoteBranches == null){
      GUIController.getInstance().errorHandler("Remote Branches konnten nicht geladen werden");
      return;
    }
    boolean containsUpStreamBranch = false;
    int count = 0;
    for (GitBranch remoteBranch : remoteBranches){
      remoteBranchComboBox.addItem(remoteBranch.getName());
      //gibt es schon einen remote upstream branch? Falls nicht, füge lokalen Branch als "Dummy" hinzu
      if (remoteBranch.getName().compareTo(localBranch.getName()) == 0){
        containsUpStreamBranch = true;
        remoteBranchComboBox.setSelectedIndex(count);
      }
      count++;
    }

    //add dummy upstream branch
    if (!containsUpStreamBranch){
      remoteBranches.add(count, localBranch);
      remoteBranchComboBox.addItem(localBranch.getName());
      remoteBranchComboBox.setSelectedIndex(count);
    }
  }

  private boolean executePush(){

    remoteBranch = remoteBranches.get(remoteBranchComboBox.getSelectedIndex());
    setUpstream = setUpstreamCheckbox.isSelected();
    remote = remoteList.get(remoteComboBox.getSelectedIndex());
    localBranch = localBranches.get(localBranchComboBox.getSelectedIndex());
    System.out.println(remote.getName());
    System.out.println(remoteBranch.getName());
    System.out.println(localBranch.getName());
    Push push = new Push();
    push.setLocalBranch(localBranch);
    push.setRemote(remote);
    /*if remote branch equals local branch, the remote upstream branch does not yet exist. The selected item from
    remoteBranchComboBox was only a dummy for the to-be-created upstream branch. So, in case they are equal, set remote
    branch to null 
     */
    if (remoteBranch.getName().compareTo(localBranch.getName()) == 0){
      push.setRemoteBranch(null);
    }
    else{
      push.setRemoteBranch(remoteBranch);
    }
    push.setSetUpstream(setUpstream);
    boolean success = false;
    success = push.execute();
    return success;
  }



  private boolean reloadBranches(GitRemote r){
    GitData git = new GitData();
    try {
      remoteBranches =  git.getBranches(r);
      return true;
    } catch (GitException e) {
      CredentialProviderHolder.getInstance().changeProvider(true, r.getName());
      if (CredentialProviderHolder.getInstance().isActive()){
        return reloadBranches(r);
      }
      else {
        CredentialProviderHolder.getInstance().setActive(true);
        return false;
      }

    }
  }
}


