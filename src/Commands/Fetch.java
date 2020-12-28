package Commands;

import java.util.List;

public class Fetch implements ICommand{

    /**
     * Returns a list containing all remote names.
     * @return a list with with remote names.
     */
    public List<String> getRemotes() {
        return null;
    }

    /**
     * Sets the selected remotes to execute the "git fetch" command.
     * @param names the names of the selected remotes.
     */
    public void setRemotes(List<String> names) {}
}
