package Commands;

public class Diff implements ICommand{

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
