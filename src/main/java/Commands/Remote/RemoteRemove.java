package Commands.Remote;

import Commands.ICommand;
import Commands.ICommandGUI;

/**
 * This command removes a remote from the list of remote repositories.
 */
public class RemoteRemove implements ICommand, ICommandGUI {

    /**
     * This method specifies the name of the remote that should be removed from the list. If the remote with the
     * given name is not in the list, execute() will return false.
     * @param name The name of the remote that should be removed.
     * @see Commands.Remote.RemoteAdd
     */
    public void setName(String name){}

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
