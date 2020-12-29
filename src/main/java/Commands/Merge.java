package Commands;

import Git.GitChangeConflict;
import Git.GitCommit;
import Levels.ILevel;

public class Merge implements ICommand {
    private final GitCommit a,b;
    /* Mode-Enum? FF?*/

    public Merge(GitCommit a, GitCommit b) {
        this.a = a;
        this.b = b;
    }

    public boolean execute(ILevel level) {
        if (!isAllowed(level)) return false;

        // do stuff

        // Open MergeDialogView

        return true;
    }

    public boolean isAllowed(ILevel level) {
        return false;
    }

    public ILevel getMinimumLevel() {
        return null;
    }

    public GitChangeConflict[] getConflicts() {
        GitChangeConflict ret[] = new GitChangeConflict[1];

        return ret;
    }
}
