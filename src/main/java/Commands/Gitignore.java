package Commands;

public class Gitignore implements ICommand, ICommandGUI {

    /**
     * Add the text pattern (could be file path or part of it) of the blob to .gitignore. Blob must be a file, so
     * consider using File, VCSFile or something similar instead
     * @param blop An object that contains file data in JGit. Probably switch to File, VCSFile etc.
     */
    public void addToGitignore(Blop blop){
        if (!file.isVersioned()){
            //add to gitignore
        }
        else{
            //do nothing
        }
    }

    /**
     * Remove the text pattern that matches the file path from .gitignore
     * @param blop An object that contains file data in JGit
     */
    public void removeFromGitignore(Blop blop){
        if (file.isIgnored()){
            //remove
        }
        else{
            //do nothing
        }
    }

    /**
     * Method to execute the command
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        return false;
    }

    /**
     * Method to get the Commandline input that would be necessarry to execute the command
     *
     * @param userInput The input that the user needs to make additionally to the standard output of git commit
     * @return Returns a String representation of the corresponding git command to display on the command line
     */
    public String getCommandLine(String userInput) {
        return null;
    }

    /**
     * Method to get the name of the command, that could be displaied in the GUI
     *
     * @return The name of the command
     */
    public String getName() {
        return null;
    }

    /**
     * Method to get a description of the Command to describe for the user, what the command does
     *
     * @return description as a Sting
     */
    public String getDescription() {
        return null;
    }
}
