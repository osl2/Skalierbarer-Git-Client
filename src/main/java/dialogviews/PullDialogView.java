package dialogviews;

import commands.Pull;
import git.CredentialProviderHolder;
import git.GitBranch;
import git.GitData;
import git.GitRemote;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PullDialogView implements IDialogView {


    private JComboBox<String> remoteCombobox;
    private JButton refreshButton;
    private JComboBox<String> branchComboBox;
    private JButton pullButton;
    private JPanel pullPanel;
    private GitData data;
    private List<GitRemote> listOfRemotes;
    private List<GitBranch> listOfRemoteBranches = new ArrayList<>();
    private Pull pull;

    public PullDialogView() {
        initPull();
        remoteCombobox.addActionListener(e -> {
            listOfRemoteBranches.clear();
            branchComboBox.removeAllItems();
            int index = remoteCombobox.getSelectedIndex();
            if (!nextTry(index)) {
                return;
            }
            for (GitBranch listOfRemoteBranch : listOfRemoteBranches) {
                branchComboBox.addItem(listOfRemoteBranch.getName());
            }
        });
        refreshButton.addActionListener(e -> {
            refresh();
            initPull();
        });
        pullButton.addActionListener(e -> {
            pull = new Pull();
            int remoteIndex = remoteCombobox.getSelectedIndex();
            pull.setRemote(listOfRemotes.get(remoteIndex));
            int branchIndex = branchComboBox.getSelectedIndex();
            pull.setRemoteBranch(listOfRemoteBranches.get(branchIndex));
            pull.execute();
        });
    }

    private boolean nextTry(int index) {
        try {
            listOfRemoteBranches = data.getBranches(listOfRemotes.get(index));
            return true;
        } catch (GitException gitException) {
            CredentialProviderHolder.getInstance().changeProvider(true, listOfRemotes.get(index).getName());
            if (CredentialProviderHolder.getInstance().isActive()) {
                return nextTry(index);
            } else {
                CredentialProviderHolder.getInstance().setActive(true);
                return false;
            }
        }
    }

    private void refresh() {
        listOfRemotes.clear();
        listOfRemoteBranches.clear();
        branchComboBox.removeAllItems();
    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Pull";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(500, 200);
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return pullPanel;
    }

    private void initPull() {
        data = new GitData();
        listOfRemotes = data.getRemotes();
        String[] remoteName = new String[listOfRemotes.size()];
        for (int i = 0; i < listOfRemotes.size(); i++) {
            remoteName[i] = listOfRemotes.get(i).getName();
        }
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(remoteName);
        remoteCombobox.setModel(model);
    }

    public void update() {
        // This method is not used because it is not needed.
    }

}