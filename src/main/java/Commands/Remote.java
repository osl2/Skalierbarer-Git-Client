package Commands;

import Git.GitRemote;

public class Remote implements ICommand, ICommandGUI {

    /**
     * Method to create new Remote
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        return false;
    }

    /**
     * Creates with the input the command of the commandline
     * @param userInput Input off the user
     * @return Returns command for Commandline
     */
    public String getCommandLine(String userInput) {
        return "git remote";
    }

    /**
     *
     * @return Returns the name of the command
     */
    public String getName() {
        return "remote";
    }

    /**
     *
     * @return Returns a Description of what the command is doing
     */
    public String getDescription() {
        return "Ermöglicht es Online-Repos zu speichern und deren URL und Namen zu ändern";
    }

    /**
     * Changes the name of the remote into the name that is given as parameter
     * @param name New name
     * @param remote Remote whose name is to be changed
     */
    public void setRemoteName(String name, GitRemote remote){

    }

    /**
     *
     * @param remote Remote whose name is to be returned
     * @return Returns name of the remote
     */
    public String getRemoteName(GitRemote remote){
        return "Name muss aus remote gewonnen werden";
    }

    /**
     * Changes the URL of the remote into the name that is given as parameter
     * @param url New URL
     * @param remote remote whose URL is to be changed
     */
    public void setRemoteURL(String url, GitRemote remote){

    }

    /**
     *
     * @param remote Remote whose Url is to be returned
     * @return Returns Url of the remote
     */
    public String getRemoteUrl(GitRemote remote){
        return "Url aus bestimmten remote";
    }
}