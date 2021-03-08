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
    private JComboBox<GitRemote> remoteComboBox;
    private JButton pushButton;
    @SuppressWarnings("unused")
    private JPanel remotePanel;
    private JButton refreshButton;
    private JComboBox<GitBranch> localBranchComboBox;
    private JTextField selectedRemoteBranchTextfield;
    @SuppressWarnings("unused")
    private JTextField remoteTextField;
    @SuppressWarnings("unused")
    private JTextField localBranchTextField;
    @SuppressWarnings("unused")
    private JTextField remoteBranchTextField;
    @SuppressWarnings("unused")
    private JPanel localBranchPanel;

    public PushDialogView() {
        loadDialog();

        localBranchComboBox.addActionListener(e -> {
            GitBranch localBranch = (GitBranch) localBranchComboBox.getSelectedItem();
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

        setNameComponents();
    }

    /*
    Prepares the comboboxes and the textfield for the remote branch
     */
    private void loadDialog() {
        //fill local branch combobox and remote combobox with values
        setUpLocalBranchComboBox();
        setUpRemoteComboBox();
        //set text of the remote branch textfield: remote branch equals selected local branch
        GitBranch selectedLocalBranch = (GitBranch) localBranchComboBox.getSelectedItem();
        if (selectedLocalBranch != null) {
            selectedRemoteBranchTextfield.setText(selectedLocalBranch.getName());
        }
    }

    /*
    Sets the name of custom created components for testing. DO NOT CHANGE, otherwise, tests might fail
     */
    private void setNameComponents() {
        remoteComboBox.setName("remoteComboBox");
        localBranchComboBox.setName("localBranchComboBox");
        selectedRemoteBranchTextfield.setName("selectedRemoteBranchTextField");
        refreshButton.setName("refreshButton");
        pushButton.setName("pushButton");

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

    /**
     * updates the view
     */
    @Override
    public void update() {
        loadDialog();
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
        List<GitBranch> localBranches;
        try {
            localBranches = gitData.getBranches();
        } catch (GitException e) {
            GUIController.getInstance().errorHandler(e);
            return;
        }
        GitBranch checkedOutBranch;
        try {
            checkedOutBranch = gitData.getSelectedBranch();
        } catch (IOException e) {
            GUIController.getInstance().errorHandler(e);
            return;
        }
        for (GitBranch branch : localBranches) {
            //add all local branches to the combobox
            localBranchComboBox.addItem(branch);
            //select by default the currently checked out branch
            if (branch.getName().compareTo((checkedOutBranch.getName())) == 0) {
                localBranchComboBox.setSelectedItem(branch);
            }
        }

        localBranchComboBox.setRenderer(new BranchComboBoxRenderer());
    }

    /*
    sets ups the combo box that contains the registered remotes. Preselects origin (if existing)
     */
    private void setUpRemoteComboBox() {
        GitData gitData = new GitData();
        //clear up the combobox
        remoteComboBox.removeAllItems();
        for (GitRemote gitRemote : gitData.getRemotes()) {
            //add all remotes in the list to the combo box
            remoteComboBox.addItem(gitRemote);
            // select origin by default
            if (gitRemote.getName().compareTo("origin") == 0) {
                remoteComboBox.setSelectedItem(gitRemote);
            }
        }

        remoteComboBox.setRenderer(new RemoteComboBoxRenderer());
    }

    /*
    Provides the push command with the necessary parameters and calls execute(). If the command was executed
    successfully, the GUIController is set to display the command line
     */
    private boolean executePush() {
        Push push = new Push();
        push.setLocalBranch((GitBranch) localBranchComboBox.getSelectedItem());
        push.setRemote((GitRemote) remoteComboBox.getSelectedItem());

        push.setRemoteBranch(selectedRemoteBranchTextfield.getText());
        boolean success = push.execute();

        //set command line if push was successful
        if (success) {
            GUIController.getInstance().setCommandLine(push.getCommandLine());
        }
        return success;
    }

    /*
    Renderer for branch combobox. Displays the name of the branch
     */
    private class BranchComboBoxRenderer extends JTextField implements ListCellRenderer<GitBranch> {
        @Override
        public Component getListCellRendererComponent(JList<? extends GitBranch> list, GitBranch value, int index, boolean isSelected, boolean cellHasFocus) {
            this.setText(value.getName());
            return this;
        }
    }

    /*
    Renderer for remote combobox. Displays the name of the remote
     */
    private class RemoteComboBoxRenderer extends JTextField implements ListCellRenderer<GitRemote> {
        @Override
        public Component getListCellRendererComponent(JList<? extends GitRemote> list, GitRemote value, int index, boolean isSelected, boolean cellHasFocus) {
            this.setText(value.getName());
            return this;
        }
    }
}


