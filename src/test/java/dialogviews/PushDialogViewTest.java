package dialogviews;

import commands.AbstractRemoteTest;
import git.GitBranch;
import git.GitRemote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PushDialogViewTest extends AbstractRemoteTest {
    private PushDialogView pushDialogView;
    private JPanel pushPanel;
    private JComboBox remoteComboBox;
    private JComboBox localBranchComboBox;
    private JTextField selectedRemoteBranchTextField;
    private JButton refreshButton;
    private JButton pushButton;
    private FindComponents find;

    @BeforeEach
    void prepareDialog() {
        find = new FindComponents();
        pushDialogView = new PushDialogView();
        pushPanel = pushDialogView.getPanel();
        remoteComboBox = (JComboBox) find.getChildByName(pushPanel, "remoteComboBox");
        localBranchComboBox = (JComboBox) find.getChildByName(pushPanel, "localBranchComboBox");
        selectedRemoteBranchTextField = (JTextField) find.getChildByName(pushPanel, "selectedRemoteBranchTextField");
        refreshButton = (JButton) find.getChildByName(pushPanel, "refreshButton");
        pushButton = (JButton) find.getChildByName(pushPanel, "pushButton");

    }

    @Test
    void loadDialogViewTest() {
        assertNotNull(pushPanel);
        assertNotNull(remoteComboBox);
        assertNotNull(localBranchComboBox);
        assertNotNull(selectedRemoteBranchTextField);
        assertNotNull(refreshButton);
        assertNotNull(pushButton);

        assertEquals(0, selectedRemoteBranchTextField.getText().compareTo("master"));
    }

    @Test
    void testGetTitle() {
        assertNotNull(pushDialogView.getTitle());
    }

    @Test
    void testGetDimension() {
        assertNotNull(pushDialogView.getDimension());
    }

    @Test
    void localBranchComboBoxTest() {
        //master should be the only local branch
        assertEquals(1, localBranchComboBox.getModel().getSize());
        //select the master branch
        localBranchComboBox.setSelectedIndex(0);
        for (ActionListener listener : localBranchComboBox.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(localBranchComboBox, ActionEvent.ACTION_PERFORMED, "Action event"));
        }
        assertEquals(0, selectedRemoteBranchTextField.getText().compareTo("master"));

        //master should be selected
        GitBranch localBranch = (GitBranch) localBranchComboBox.getSelectedItem();
        assertNotNull(localBranch);
        assertEquals(0, localBranch.getName().compareTo("master"));
    }

    @Test
    void remoteComboBoxTest() {
        //should contain one remote
        assertEquals(1, remoteComboBox.getModel().getSize());

        //origin should be selected
        GitRemote remote = (GitRemote) remoteComboBox.getSelectedItem();
        assertNotNull(remote);
        assertEquals(0, remote.getName().compareTo("origin"));
    }

    @Test
    void updateTest() {
        pushDialogView.update();
        loadDialogViewTest();
        //TODO: add new remotes and reload
    }
}
