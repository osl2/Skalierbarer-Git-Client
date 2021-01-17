package dialogviews;

/**
 * This class represents the default error dialog which takes an error message after execute() has failed and displays
 * it to the user. Its only button is "Okay" which closes the dialog.
 */
public class DefaultErrorDialogView implements IErrorDialogView {
    /**
     * This constructor takes the error message to display to the user.
     * @param errorMessage A description of why execute() failed and the dialog was opened
     */
    public DefaultErrorDialogView(String errorMessage){
    }

    public void setErrorMessage(String errorMessage) {

    }

    public void show() {

    }

    public void update() {

    }
}
