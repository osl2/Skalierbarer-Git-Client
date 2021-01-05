package Commands;

import Git.GitBlob;

import java.util.List;

public class AddGitignore implements ICommand {

    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    /**
     * This method specifies the names or file paths of files that should be added to the .gitignore. When execute()
     * is invoked, all patterns in the list are added to the .gitignore file.
     * @param regexes A list of regular expressions whose paths should be added to the .gitignore
     * TODO: consider using a list of blobs (concrete files) instead?
     */
    public void setRegex(List<String> regexes){
    }
}
