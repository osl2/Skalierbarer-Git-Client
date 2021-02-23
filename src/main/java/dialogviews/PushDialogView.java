package dialogviews;

import commands.Push;
import controller.GUIController;
import git.GitBranch;
import git.GitData;
import git.GitRemote;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class PushDialogView implements IDialogView {

    private final GitData gitData;
    private JPanel contentPane;
    private JComboBox<String> remoteComboBox;
    private JButton pushButton;
    private JPanel remotePanel;
    private JButton refreshButton;
    private JComboBox<String> localBranchComboBox;
    private JTextField branchnameTextfield;
    private GitBranch localBranch;
    private GitRemote remote;
    private String remoteBranch;
    private List<GitBranch> localBranches;
    private List<GitRemote> remoteList;

    public PushDialogView() {
        gitData = new GitData();
        setUpLocalBranchComboBox();
        setUpRemoteComboBox();
        branchnameTextfield.setText(localBranches.get(localBranchComboBox.getSelectedIndex()).getName());

        localBranchComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = localBranchComboBox.getSelectedIndex();
                localBranch = localBranches.get(index);
                branchnameTextfield.setText(localBranch.getName());
                //if remote and local branch have been set, setup the remote branch combo box
            }
        });
        pushButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //try to execute push. If successful, close the push dialog view
                if (executePush()) {

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
    }
    /*
    setup the combo box that contains the local branches. Preselects the branch that is currently checked out
     */
    private void setUpLocalBranchComboBox() {
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
        for (GitBranch branch : localBranches) {

            localBranchComboBox.addItem(branch.getName());
            if (branch.getName().compareTo((selectedBranch.getName())) == 0) {
                localBranchComboBox.setSelectedIndex(count);
            }
            count++;
        }

    }

    /*
    sets ups the combo box that contains the registered remotes. Preselects origin (if existing)
     */
    private void setUpRemoteComboBox() {
        remoteList = gitData.getRemotes();
        remoteComboBox.removeAllItems();
        int count = 0;
        for (GitRemote remote : remoteList) {
            remoteComboBox.addItem(remote.getName());
            if (remote.getName().compareTo("origin") == 0) {
                remoteComboBox.setSelectedIndex(count);
            }
            count++;
        }
    }

    private boolean executePush() {

        remoteBranch = branchnameTextfield.getText();
        remote = remoteList.get(remoteComboBox.getSelectedIndex());
        localBranch = localBranches.get(localBranchComboBox.getSelectedIndex());
        Push push = new Push();
        push.setLocalBranch(localBranch);
        push.setRemote(remote);
    /*if remote branch equals local branch, the remote upstream branch does not yet exist. The selected item from
    remoteBranchComboBox was only a dummy for the to-be-created upstream branch. So, in case they are equal, set remote
    branch to null
     */
        if (remoteBranch.compareTo(localBranch.getName()) == 0) {
            push.setRemoteBranch(null);
        } else {
            push.setRemoteBranch(remoteBranch);
        }
        boolean success = false;
        success = push.execute();
        if (success) {
            GUIController.getInstance().setCommandLine(push.getCommandLine());
        }
        return success;
    }

    private class BranchComboBoxRenderer extends JTextField implements ListCellRenderer<GitBranch> {


        @Override
        public Component getListCellRendererComponent(JList<? extends GitBranch> list, GitBranch value, int index, boolean isSelected, boolean cellHasFocus) {
            GitBranch branch = (GitBranch) value;
            this.setText(branch.getName());
            return this;
        }
    }

    private class RemoteComboBoxRenderer extends JTextField implements ListCellRenderer<GitRemote> {

        @Override
        public Component getListCellRendererComponent(JList<? extends GitRemote> list, GitRemote value, int index, boolean isSelected, boolean cellHasFocus) {
            GitRemote remote = (GitRemote) value;
            this.setText(remote.getName());
            return this;
        }
    }
}


