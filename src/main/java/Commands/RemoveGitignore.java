package Commands;

import Commands.ICommand;

public class RemoveGitignore implements ICommand {
    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
