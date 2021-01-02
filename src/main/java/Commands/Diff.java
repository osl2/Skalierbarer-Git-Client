package Commands;

public class Diff implements ICommand{

    /**
     * Executes the "git diff" command. Can only be used after setDiffID was called once.
     * @return true, if the command has been executed successfully
     */
    public boolean execute() {
        //not implemeneted yet
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    /**
     * Sets the commit ID and the file name to compare with the previous one.
     * @param commitID the ID of the selected commit.
     * @param fileName the name of the file to compare to his previous version.
     */
    public void setDiffID(String commitID, String fileName) {}

    /**
     * Sets the output format of the "git diff" command.
     * @param output constant that provides information on the output format.
     */
    public void showDiff(int output) {}
}
