package Commands.Remote;

import Commands.ICommand;
import Commands.ICommandGUI;

import java.net.URL;

/**
 * This command adds a new remote repository. Before execution, the user needs to define a name and the URL.
 * Both parameters can be configured later.
 * @see Commands.Remote
 */
public class RemoteAdd implements ICommand, ICommandGUI {
    public void setName(String remoteName){}

    public void setURL(URL remoteURL){}

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
        return "git remote add";
    }

    @Override
    public String getName() {
        return "remote add";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void onButtonClicked() {

    }
}
