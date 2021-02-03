package dialogviews;

import javax.swing.*;
import java.awt.*;

public class CheckoutDialogView implements IDialogView {

    private JPanel contentPane;

    private JTree tree1;
    private JButton abortButton;
    private JButton okButton;

    public CheckoutDialogView() {
        // Todo: localize
        this.abortButton.setText("Abbrechen");

        // Todo: localize
        this.okButton.setText("Ok");
    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Titel";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(400, 600);
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return this.contentPane;
    }

    /**
     * method that ist called to update the dialog view.
     */
    public void update() {

  }
}