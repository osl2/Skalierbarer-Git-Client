package Commands.Remote;

import Commands.ICommand;
import Commands.ICommandGUI;
import Git.GitRemote;

public abstract class Remote implements ICommand, ICommandGUI {
    /**
     * @param remote The remote repository on which the command should be executed (getName, getURL, setName, setURL, remove)
     */
    public abstract void setRemote(GitRemote remote);

    /**
     * @return The current remote repository
     */
    public abstract GitRemote getRemote();





    /**
     * Method to create new Remote
     *
     * @return true, if the command has been executed successfully
     */
    public abstract boolean execute();

    /**
     * Creates with the input the command of the commandline
     *
     * @param userInput Input off the user
     * @return Returns command for Commandline
     */
    public abstract String getCommandLine(String userInput);

    /**
     * @return Returns the name of the command
     */
    public abstract String getName();

    /**
     * @return Returns a Description of what the command is doing
     */
    public abstract String getDescription();


}