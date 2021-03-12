package dialogviews;

import commands.AbstractCommandTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import settings.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class FirstUseDialogViewTest extends AbstractCommandTest {
    private FirstUseDialogView firstUseDialogView;
    private JPanel panel;

    @BeforeEach
    void prepare() {
        firstUseDialogView = new FirstUseDialogView();
        panel = firstUseDialogView.getPanel();
    }


    @Test
    void testViewData() {
        assertEquals(0, firstUseDialogView.getTitle().compareTo("Erstbenutzung"));
        assertEquals(500, firstUseDialogView.getDimension().width);
        assertEquals(150, firstUseDialogView.getDimension().height);
        assertNotNull(firstUseDialogView.getPanel());
    }

    @Test
    void testFinishButton() {
        //prevent fileChooser from opening window
        MockedConstruction<JFileChooser> mockedJFileChooser = mockConstruction(JFileChooser.class, (mock, context) -> {
            when(mock.showOpenDialog(any())).thenReturn(0);
            when(mock.getSelectedFile()).thenReturn(repo);
        });

        JButton chooseButton = (JButton) FindComponents.getChildByName(panel, "chooseButton");
        assertNotNull(chooseButton);
        for (ActionListener listener : chooseButton.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(chooseButton, ActionEvent.ACTION_PERFORMED, "Button clicked"));
        }

        JButton finishButton = (JButton) FindComponents.getChildByName(panel, "finishButton");
        assertNotNull(finishButton);
        for (ActionListener listener : finishButton.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(finishButton, ActionEvent.ACTION_PERFORMED, "Button clicked"));
        }
        assertEquals(0, Settings.getInstance().getActiveRepositoryPath().getName().compareTo(repo.getName()));

        assertTrue(guiControllerTestable.closeDialogViewCalled);

        mockedJFileChooser.close();

    }

}
