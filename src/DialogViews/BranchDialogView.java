public class BranchDialogView implements IDialogView {
    private LinkedList<Branch> branches = new LinkedList<Branch>();
    private LinkedList<Commit> commits = new LinkedList<Commit>();
    private String nameOfNew;

    /**
     *
     * @return Returns the list of the existing branches
     */
    public LinkedList<Branch> getBranches() {
        return branches;
    }

    /**
     *
     * @param branches Input is a new list of branches
     */
    public void setBranches(LinkedList<Branch> branches) {
        this.branches = branches;
    }

    /**
     *
     * @return Returns list of current commits
     */
    public LinkedList<Commit> getCommits() {
        return commits;
    }

    /**
     *
     * @param commits Input is new List of commits
     */
    public void setCommits(LinkedList<Commit> commits) {
        this.commits = commits;
    }

    /**
     *
     * @return Retruns name of the new Branch
     */
    public String getNameOfNew() {
        return nameOfNew;
    }

    /**
     *
     * @param nameOfNew Input is a new name of the next branch (Excetion if "?")
     */
    public void setNameOfNew(String nameOfNew) {
        this.nameOfNew = nameOfNew;
    }
}