package views.filter;

import git.GitAuthor;
import git.GitCommit;

/**
 * Filter {@link views.HistoryView} by {@link git.GitAuthor}
 */
public class HistoryAuthorFilter extends AbstractHistoryFilter {

    private final GitAuthor author;

    public HistoryAuthorFilter(GitAuthor author) {
        this.author = author;
    }

    protected boolean isMatch(GitCommit commit) {
        return commit.getAuthor().equals(this.author);
    }
}
