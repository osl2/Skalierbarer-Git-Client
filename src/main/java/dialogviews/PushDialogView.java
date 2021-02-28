package dialogviews;

import commands.Push;
import controller.GUIController;
import git.GitBranch;
import git.GitData;
import git.GitRemote;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class PushDialogView implements IDialogView {

    private JPanel contentPane;
    private JComboBox<String> remoteComboBox;
    private JButton pushButton;
    @SuppressWarnings("unused")
    private JPanel remotePanel;
    private JButton refreshButton;
    private JComboBox<String> localBranchComboBox;
    private JTextField selectedRemoteBranchTextfield;
    @SuppressWarnings("unused")
    private JTextField remoteTextField;
    @SuppressWarnings("unused")
    private JTextField localBranchTextField;
    @SuppressWarnings("unused")
    private JTextField remoteBranchTextField;
    @SuppressWarnings("unused")
    private JPanel localBranchPanel;
    private GitBranch localBranch;
    private List<GitBranch> localBranches;
    private List<GitRemote> remoteList;

    public PushDialogView() {
        //fill local branch combobox and remote combobox with values
        setUpLocalBranchComboBox();
        setUpRemoteComboBox();
        //set text of the remote branch textfield: remote branch equals selected local branch
        selectedRemoteBranchTextfield.setText(localBranches.get(localBranchComboBox.getSelectedIndex()).getName());

        localBranchComboBox.addActionListener(e -> {
            //TODO: combobox enthält Strings
            int index = localBranchComboBox.getSelectedIndex();
            localBranch = localBranches.get(index);
            if (localBranch != null){
                selectedRemoteBranchTextfield.setText(localBranch.getName());
            }
        });
        pushButton.addActionListener(e -> {
            //try to execute push. If successful, close the push dialog view
            if (executePush()) {
                GUIController.getInstance().closeDialogView();
            }
        });

        //refresh the list of remotes and remote branches.
        refreshButton.addActionListener(e -> setUpRemoteComboBox());
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

    @SuppressWarnings("unused")
    private void createUIComponents() {
        localBranchComboBox = new JComboBox<>();
        remoteComboBox = new JComboBox<>();
    }
    /*
    setup the combo box that contains the local branches. Preselects the branch that is currently checked out
     */
    private void setUpLocalBranchComboBox() {
        GitData gitData = new GitData();
        try {
            localBranches = gitData.getBranches();
        } catch (GitException e) {
            GUIController.getInstance().errorHandler(e);
            return;
        }
        GitBranch selectedBranch;
        try {
            selectedBranch = gitData.getSelectedBranch();
        } catch (IOException e) {
            GUIController.getInstance().errorHandler(e);
            return;
        }
        int count = 0;
        for (GitBranch branch : localBranches) {
            //add all local branches to the combobox
            localBranchComboBox.addItem(branch.getName());
            //select by default the currently checked out branch
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
        GitData gitData = new GitData();
        remoteList = gitData.getRemotes();
        //clear up the combobox
        remoteComboBox.removeAllItems();
        int count = 0;
        for (GitRemote gitRemote : remoteList) {
            //add all remotes in the list to the combo box
            remoteComboBox.addItem(gitRemote.getName());
            // select origin by default
            if (gitRemote.getName().compareTo("origin") == 0) {
                remoteComboBox.setSelectedIndex(count);
            }
            count++;
        }
    }

    private boolean executePush() {
        GitRemote remote = remoteList.get(remoteComboBox.getSelectedIndex());
        localBranch = localBranches.get(localBranchComboBox.getSelectedIndex());
        Push push = new Push();
        push.setLocalBranch(localBranch);
        push.setRemote(remote);

        push.setRemoteBranch(null);

        boolean success = push.execute();
        if (success) {
            GUIController.getInstance().setCommandLine(push.getCommandLine());
        }
        return success;
    }


}


