package Commands.Remote;

import Commands.ICommand;
import Commands.ICommandGUI;

/**
 * This command sets the URL parameter of the remote repository to a new URL
 */
public class RemoteSetURL implements ICommand, ICommandGUI {
    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public String getCommandLine(String userInput) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void onButtonClicked() {

    }
}
