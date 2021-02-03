package dialogviews;

import javax.swing.*;
import java.awt.*;

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
     * @return the shown content
     */
    JPanel getPanel();

    /**
     * Refresh the contents of the Dialog window,
     * i.e. when data changes
     */
    void update();

}