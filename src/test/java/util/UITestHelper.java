package util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class UITestHelper {
    public static void clickButton(JButton button) {
        ActionEvent clickEvent = new ActionEvent(button, ActionEvent.ACTION_PERFORMED, null);
        Arrays.stream(button.getActionListeners()).forEach(l -> l.actionPerformed(clickEvent));
    }

}
