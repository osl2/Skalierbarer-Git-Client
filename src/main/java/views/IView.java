package views;

import javax.swing.*;

/**
 * Represents a view which can be loaded into the lower part of {@link MainWindow}
 */
public interface IView {

    /**
     * Returns the lower part of the main window.
     */
    JPanel getView();

    /**
     * Updates the view
     */
    void update();


}
