package dialogviews;

import commands.AbstractCommandTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;

 public class RemoteAddDialogViewTest extends AbstractCommandTest {
    JTextField textField;
    JTextField urlField;
    JButton addButton;
    JButton stopButton;
    @BeforeEach
    void getComp(){
        FindComponents find = new FindComponents();
        RemoteAddDialogView remoteD = new RemoteAddDialogView();
        JPanel frame = remoteD.getPanel();
        textField = (JTextField) find.getChildByName(frame, "namefield");
        assertNotNull(textField);
        urlField = (JTextField) find.getChildByName(frame, "urlField");
        assertNotNull(urlField);
        addButton = (JButton) find.getChildByName(frame, "addButton");
        assertNotNull(addButton);
        stopButton =(JButton) find.getChildByName(frame, "stopButton");
        assertNotNull(stopButton);
    }
    @Test
    void testRemoteAddDialogView1()  {
        textField.setText("");
        addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }
    @Test
    void testRemoteAddNoUrl() {
        textField.setText("test");
        urlField.setText("");
        addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.errorHandlerECalled);
    }
    @Test
    void stopButtonTest(){
        stopButton.getActionListeners()[0].actionPerformed(new ActionEvent(stopButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.closeDialogViewCalled);
    }
    @Test
    void addButtonTest(){
        textField.setText("abc");
        urlField.setText("abc");
        addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.setCommandLineCalled);
        textField.setText("abc");
        urlField.setText("abc");
        addButton.getActionListeners()[0].actionPerformed(new ActionEvent(addButton, ActionEvent.ACTION_PERFORMED, null));
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }
    @Test
    void titleTest(){
        RemoteAddDialogView view = new RemoteAddDialogView();
        view.update();
        assertNotNull(view.getTitle());
        assertNotNull(view.getDimension());
        assertNotNull(view.getPanel());

    }
}
