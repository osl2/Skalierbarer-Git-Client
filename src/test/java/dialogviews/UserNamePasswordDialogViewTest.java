package dialogviews;

import commands.AbstractCommandTest;
import git.CredentialProviderHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class UserNamePasswordDialogViewTest extends AbstractCommandTest {
    private final String NAME = "Name";
    private UsernamePasswordDialogView usernamePasswordDialogView;
    private JButton breakButton;
    private JButton okButton;

    @BeforeEach
    void prepare() {
        usernamePasswordDialogView = new UsernamePasswordDialogView(NAME);
        JPanel panel = usernamePasswordDialogView.getPanel();
        breakButton = (JButton) FindComponents.getChildByName(panel, "breakButton");
        okButton = (JButton) FindComponents.getChildByName(panel, "okButton");

    }

    @Test
    void testDialogData() {
        assertEquals(0, usernamePasswordDialogView.getTitle().compareTo("Benutzername - " + NAME));
        assertEquals(400, usernamePasswordDialogView.getDimension().width);
        assertEquals(200, usernamePasswordDialogView.getDimension().height);
    }

    @Test
    void okButtonTest() {
        assertNotNull(okButton);
        for (ActionListener listener : okButton.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(okButton, ActionEvent.ACTION_PERFORMED, "OK button clicked"));
        }
        assertTrue(CredentialProviderHolder.getInstance().isActive());
        assertTrue(guiControllerTestable.closeDialogViewCalled);
    }

    @Test
    void breakButtonTest() {
        assertNotNull(breakButton);
        for (ActionListener listener : breakButton.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(breakButton, ActionEvent.ACTION_PERFORMED, "Break button clicked"));
        }
        assertFalse(CredentialProviderHolder.getInstance().isActive());
        assertTrue(guiControllerTestable.closeDialogViewCalled);
    }
}
