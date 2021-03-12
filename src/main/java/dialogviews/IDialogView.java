package dialogviews;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a Dialog Window
 * the Information is used by the {@link controller.GUIController}
 * to create a new modal Dialog Window.
 */
public interface IDialogView {

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    String getTitle();

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    Dimension getDimension();

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the contentPanel of the new Dialog
     */
    JPanel getPanel();

    /**
     * Called to notify the Dialog of changes to the application data.
     * A well written Dialog should reload all data shown to the user.
     */
    void update();

}