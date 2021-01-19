package views.filter;

import git.GitCommit;

/**
 * This class is the default filter of {@link views.HistoryView}
 * it makes no changes to the list of {@link git.GitCommit} it receives.
 */
public class AllHistoryFilter extends AbstractHistoryFilter {

    protected boolean isMatch(GitCommit commit) {
        /* TODO: Naive implementation; make faster */
        return true;
    }
}
