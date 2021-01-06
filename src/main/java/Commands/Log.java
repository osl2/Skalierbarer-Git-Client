package Commands;

import java.util.Date;
import java.util.List;

public class Log implements ICommand, ICommandGUI{

    //-----------------Display Options for log----------------

    public void setSinceDate(Date sinceDate){}

    public void setUntilDate(Date untilDate){}

    public void setAuthor(String author){}

    public void setGrepPattern(String grepPattern){}

    //------------------------------------------------------

    /**
     * Returns a list of commit IDs beginning with the latest commit of the given branch.
     * @param branchName name of the respective branch.
     * @return a list of commit IDs.
     */
    public List<String> getCommitIDs(String branchName) {return null;}

    /**
     * Returns a list of commit messages beginning with the latest commit message of the given branch.
     * @param branchName name of the respective branch.
     * @return a list of commit messages.
     */
    public List<String> getCommitMessages(String branchName) {return null;}

    /**
     * Method to execute the command
     *
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        //not implemented yet
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    /**
     * Method to get the Commandline input that would be necessary to execute the command
     *
     * @param userInput The input that the user needs to make additionally to the standard output of git commit
     * @return Returns a String representation of the corresponding git command to display on the command line
     */
    public String getCommandLine(String userInput) {
        return null;
    }

    /**
     * Method to get the name of the command, that could be displayed in the GUI
     *
     * @return The name of the command
     */
    public String getName() {
        return null;
    }

    /**
     * Method to get a description of the Command to describe for the user, what the command does
     *
     * @return description as a String
     */
    public String getDescription() {
        return null;
    }

    @Override
    public void onButtonClicked() {

    }
}
