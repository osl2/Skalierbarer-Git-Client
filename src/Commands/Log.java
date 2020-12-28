package Commands;

import java.util.List;

public class Log implements ICommand{

    /**
     * Returns a list of commit IDs beginning with the latest commit of the given branch.
     * @param branchName name of the respective branch.
     * @return a list of commit IDs.
     */
    public List<String> getCommitIDs(String branchName) {return null;}

    /**
     * Returns a list of commit massages beginning with the latest commit massage of the given branch.
     * @param branchName name of the respective branch.
     * @return a list of commit massages.
     */
    public List<String> getCommitMassages(String branchName) {return null;}
}
