package Commands.Remote;

import Commands.ICommand;
import Commands.ICommandGUI;

/**
 * This command removes a remote from the list of remote repositories.
 */
public class RemoteRemove implements ICommand, ICommandGUI {
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
