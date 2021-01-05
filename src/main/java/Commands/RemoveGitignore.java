package Commands;

import Commands.ICommand;

import java.util.List;

public class RemoveGitignore implements ICommand {
    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    /**
     * This method specifies the regexes that should be removed from the .gitignore file. This should only be possible
     * if the given regexes have been added to the .gitignore before. Otherwise, execute() will do nothing/ return false?
     * @param regexes
     */
    public void setRegexes(List<String> regexes){
    }
}
