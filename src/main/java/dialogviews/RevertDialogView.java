package dialogviews;

import javax.swing.*;
import java.awt.*;

public class RevertDialogView implements IDialogView {


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

    /**
     * Refresh the contents of the Dialog window,
     * i.e. when data changes
     */
    public void update() {

    }
}
