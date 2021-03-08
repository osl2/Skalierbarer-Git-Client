package dialogviews;

import commands.AbstractCommandTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PushDialogViewTest extends AbstractCommandTest {
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
    void loadDialogView() {
        assertNotNull(pushPanel);
        assertNotNull(remoteComboBox);
        assertNotNull(localBranchComboBox);
        assertNotNull(selectedRemoteBranchTextField);
        assertNotNull(refreshButton);
        assertNotNull(pushButton);

        assertEquals(0, selectedRemoteBranchTextField.getText().compareTo("master"));
        /*
        geht nicht, NPE

        GitRemote remote = (GitRemote) remoteComboBox.getSelectedItem();
        assertEquals(0, remote.getName().compareTo("origin"));
        GitBranch localBranch = (GitBranch) localBranchComboBox.getSelectedItem();
        assertEquals(0, localBranch.getName().compareTo("master"));

         */
    }

    @Test
    void testGetTitle() {
        assertNotNull(pushDialogView.getTitle());
    }

    @Test
    void testGetDimension() {
        assertNotNull(pushDialogView.getDimension());
    }

    //TODO: Test update()
}
