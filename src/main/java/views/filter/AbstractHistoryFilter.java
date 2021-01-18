package views.filter;

import git.GitCommit;

import java.util.Collection;

public abstract class AbstractHistoryFilter {

    /**
     * Applies {@link #isMatch(GitCommit)} to the Input
     *
     * @param commits Collection of Commits to be filtered
     * @return The filtered list of Commits with same order as input
     */
    public Collection<GitCommit> apply(Collection<GitCommit> commits) {
        return null;
    }

    protected abstract boolean isMatch(GitCommit commit);

}
