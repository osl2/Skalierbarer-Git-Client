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
     * This method adds the files that should be ignored to the .gitignore file.
     * @param blobs A list of GitBlobs whose paths should be added to the .gitignore
     * TODO: reconsider using a list of Strings instead, since the .gitignore may also contain RegExes
     */
    public void setBlobs(List<GitBlob> blobs){
    }
}
