package dialogviews;

import javax.swing.*;
import java.awt.*;

/**
 * This class represents the default error dialog which takes an error message after execute() has failed and displays
 * it to the user. Its only button is "Okay" which closes the dialog.
 */
public class DefaultErrorDialogView implements IDialogView {
    /**
     * This constructor takes the error message to display to the user.
     *
     * @param errorMessage A description of why execute() failed and the dialog was opened
     */
    public DefaultErrorDialogView(String errorMessage) {
    }

    public void setErrorMessage(String errorMessage) {

    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return null;
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return null;
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return null;
    }

    public void update() {

    }
}
