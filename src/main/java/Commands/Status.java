package Commands;

public class Status implements ICommand {
    /**
     * Method to execute the command
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
