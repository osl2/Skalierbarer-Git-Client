package util;

import javax.swing.*;
import java.awt.*;

public class JOptionPaneTestable extends JOptionPane {
    private static boolean showConfirmDialogCalled;

    /*
    This method always returns 0 (confirm)
     */
    public static int showConfirmDialog(Component parentComponent,
                                        Object message, String title, int optionType, int messageType)
            throws HeadlessException {

        showConfirmDialogCalled = true;
        return JOptionPane.YES_OPTION;
    }

    public void resetTestStatus() {
        showConfirmDialogCalled = false;
    }

    public boolean isShowConfirmDialogCalled() {
        return showConfirmDialogCalled;
    }
}
