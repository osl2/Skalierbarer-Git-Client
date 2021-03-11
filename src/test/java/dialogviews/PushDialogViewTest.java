package dialogviews;

import commands.AbstractRemoteTest;
import git.GitBranch;
import git.GitRemote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class PushDialogViewTest extends AbstractRemoteTest {
    private PushDialogView pushDialogView;
    private JPanel pushPanel;
    private JComboBox remoteComboBox;
    private JComboBox localBranchComboBox;
    private JTextField selectedRemoteBranchTextField;
    private JButton refreshButton;
    private JButton pushButton;

    @BeforeEach
    void prepareDialog() {
        pushDialogView = new PushDialogView();
        pushPanel = pushDialogView.getPanel();
        remoteComboBox = (JComboBox) FindComponents.getChildByName(pushPanel, "remoteComboBox");
        localBranchComboBox = (JComboBox) FindComponents.getChildByName(pushPanel, "localBranchComboBox");
        selectedRemoteBranchTextField = (JTextField) FindComponents.getChildByName(pushPanel, "selectedRemoteBranchTextField");
        refreshButton = (JButton) FindComponents.getChildByName(pushPanel, "refreshButton");
        pushButton = (JButton) FindComponents.getChildByName(pushPanel, "pushButton");

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

    @Test
    void pushButtonTest() {
        assertTrue(localBranchComboBox.getModel().getSize() > 0);
        assertTrue(remoteComboBox.getModel().getSize() > 0);
        localBranchComboBox.setSelectedIndex(0);
        remoteComboBox.setSelectedIndex(0);
        for (ActionListener listener : pushButton.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(pushButton, ActionEvent.ACTION_PERFORMED, "Button clicked"));
        }
        assertTrue(guiControllerTestable.closeDialogViewCalled);
        assertTrue(guiControllerTestable.setCommandLineCalled);
    }
}
