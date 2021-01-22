package views;

import javax.swing.*;

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
