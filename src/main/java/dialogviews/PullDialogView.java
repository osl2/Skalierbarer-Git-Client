package dialogviews;

import commands.Pull;
import git.GitBranch;
import git.GitData;
import git.GitRemote;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PullDialogView implements IDialogView {


    private JComboBox remoteCombobox;
    private JButton refreshButton;
    private JComboBox branchComboBox;
    private JButton pullButton;
    private JPanel pullPanel;
    private GitData data;
    private List<GitRemote> listOfRemotes;
    private List<GitBranch> listOfRemoteBranches = new ArrayList<GitBranch>();
    private Pull pull;

    public PullDialogView() {
        initPull();
        remoteCombobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listOfRemoteBranches.clear();
                branchComboBox.removeAllItems();
                int index = remoteCombobox.getSelectedIndex();
                listOfRemoteBranches = data.getBranches(listOfRemotes.get(index));
                for(int i = 0; i < listOfRemoteBranches.size(); i++) {
                    branchComboBox.addItem(listOfRemoteBranches.get(i).getName());
                }
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
                initPull();
            }
        });
        pullButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pull = new Pull();
                int remoteIndex = remoteCombobox.getSelectedIndex();
                pull.setRemote(listOfRemotes.get(remoteIndex));
                int branchIndex = branchComboBox.getSelectedIndex();
                pull.setRemoteBranch(listOfRemoteBranches.get(branchIndex));
                pull.execute();
            }
        });
    }

    private void initPull() {
        data = new GitData();
        listOfRemotes = data.getRemotes();
        String[] remoteName = new String[listOfRemotes.size()];
        for(int i = 0; i < listOfRemotes.size(); i++) {
            remoteName[i] = listOfRemotes.get(i).getName();
        }
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(remoteName);
        remoteCombobox.setModel(model);
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
        Dimension dim = new Dimension(500, 200);
        return dim;
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

    public void update() {

    }
}