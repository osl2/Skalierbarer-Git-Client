package Commands;

/**
 * This command executes git commit -amend to undo the last commit
 */
public class CommitUndo implements ICommand, ICommandGUI {

    /**
     * This method sets the new commitMessage
     * @param commitMessage The new message
     */
    public void setCommitMessage(String commitMessage){

    }

    /**
     * performs git commit --amend with all the blobs from the last commit plus 
     * @return
     */
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
