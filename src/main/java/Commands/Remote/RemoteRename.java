package Commands.Remote;

import Commands.ICommand;
import Commands.ICommandGUI;

/**
 * This command changes the name of the remote repository.
 */
public class RemoteRename implements ICommand, ICommandGUI {
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
