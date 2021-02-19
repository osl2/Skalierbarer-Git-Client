package dialogviews;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
  private JComboBox<GitBranch> localBranchComboBox;
  private GitData gitData;
  private GitBranch localBranch;
  private GitRemote remote;
  private GitBranch remoteBranch;
  private boolean setUpstream;
  private boolean open;

  public PushDialogView() {
    gitData = new GitData();
    setUpLocalBranchComboBox();
    setUpRemoteComboBox();

    localBranchComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JComboBox<GitBranch> source = (JComboBox) e.getSource();
        localBranch = (GitBranch) source.getSelectedItem();
        //if remote and local branch have been set, setup the remote branch combo box
        if(localBranch != null && remote != null){
          setUpRemoteBranchComboBox();
        }
      }
    });
    remoteComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JComboBox<GitRemote> source = (JComboBox) e.getSource();
        remote = (GitRemote) source.getSelectedItem();
        //if remote and local branch have been set, setup the remote branch combo box
        if(localBranch != null && remote != null){
          setUpRemoteBranchComboBox();
        }
      }
    });
    remoteBranchComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JComboBox<GitBranch> source = (JComboBox<GitBranch>) e.getSource();
        remoteBranch = (GitBranch) source.getSelectedItem();
      }
    });
    setUpstreamCheckbox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JCheckBox source = (JCheckBox) e.getSource();
        setUpstream = source.isSelected();
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
    localBranchComboBox.setRenderer(new BranchComboBoxRenderer());
    DefaultComboBoxModel<GitBranch> model = new DefaultComboBoxModel<>();
    List<GitBranch> localBranches = null;
    try {
      localBranches = gitData.getBranches();
    } catch (GitException e) {
      //TODO: do something
    }
    GitBranch selectedBranch = null;
    try {
      selectedBranch = gitData.getSelectedBranch();
    } catch (IOException e) {
      e.printStackTrace();
    }
    //TODO: wieso schlägt es bei model.addAll() fehl??????????
    //model.addAll(localBranches);
    for (GitBranch branch : localBranches){
      model.addElement(branch);
      if (branch.equals(selectedBranch)){
        model.setSelectedItem(branch);
      }
    }
    localBranchComboBox.setModel(model);
  }

  /*
  sets ups the combo box that contains the registered remotes. Preselects origin (if existing)
   */
  private void setUpRemoteComboBox(){
    remoteComboBox.setRenderer(new RemoteComboBoxRenderer());
    DefaultComboBoxModel<GitRemote> model = new DefaultComboBoxModel<>();
    List<GitRemote> remotes = gitData.getRemotes();
    for (GitRemote remote : remotes){
      model.addElement(remote);
      if (remote.getName().equals("origin")){
        model.setSelectedItem(remote);
      }
    }
    remoteComboBox.setModel(model);
  }

  private void setUpRemoteBranchComboBox(){
    remoteBranchComboBox.setRenderer(new BranchComboBoxRenderer());
    DefaultComboBoxModel<GitBranch> model = new DefaultComboBoxModel<>();
    GitBranch [] remoteBranches = loadRemoteBranches(remote);
    if (remoteBranches == null){
      GUIController.getInstance().errorHandler("Remote Branches konnten nicht geladen werden");
      return;
    }
    boolean containsUpStreamBranch = false;
    for (int i = 0; i < remoteBranches.length; i++){
      GitBranch remoteBranch = remoteBranches[i];
      model.addElement(remoteBranch);
      //gibt es schon einen remote upstream branch? Falls nicht, füge lokalen Branch als "Dummy" hinzu
      if (remoteBranch.getName().equals(localBranch.getName())){
        containsUpStreamBranch = true;
        model.setSelectedItem(remoteBranch);
      }
    }

    //add dummy upstream branch
    if (!containsUpStreamBranch){
      model.addElement(localBranch);
      model.setSelectedItem(localBranch);
    }
    remoteBranchComboBox.setModel(model);
  }

  private boolean executePush(){
    Push push = new Push();
    push.setLocalBranch(localBranch);
    push.setRemote(remote);
    /*if remote branch equals local branch, the remote upstream branch does not yet exist. The selected item from
    remoteBranchComboBox was only a dummy for the to-be-created upstream branch. So, in case they are equal, set remote
    branch to null 
     */
    if (remoteBranch.equals(localBranch)){
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

  private GitBranch[] loadRemoteBranches(GitRemote r){

    return reloadBranches(r);

  }

  private GitBranch[] reloadBranches(GitRemote r){
    GitData git = new GitData();
    GitBranch[] ret = null;
    try {
      ret =  git.getBranches(r).toArray(new GitBranch[git.getBranches(r).size()]);
    } catch (GitException e) {
      CredentialProviderHolder.getInstance().changeProvider(true, r.getName());
      if (CredentialProviderHolder.getInstance().isActive()){
        return  loadRemoteBranches(r);
      }
      else {
        open = false;
      }

    }
    return ret;
  }
}


