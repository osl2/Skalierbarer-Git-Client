package dialogviews;

/**
 * This dialog opens when execute() returns false. It should at least present the error message which caused
 * the command to fail.
 */
public interface IErrorDialog extends IDialog {

    /**
     * Passes the error message which indicates why execute() failed (if execute() = false) to the error dialog.
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage);
}
