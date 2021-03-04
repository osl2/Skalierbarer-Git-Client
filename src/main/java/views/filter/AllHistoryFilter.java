package views.filter;

import git.GitCommit;
import views.HistoryView;

/**
 * This class is the default filter of {@link HistoryView}
 * it makes no changes to the list of {@link git.GitCommit} it receives.
 */
public class AllHistoryFilter extends AbstractHistoryFilter {

    @Override
    protected boolean isMatch(GitCommit commit) {
        /* TODO: Naive implementation; make faster */
        return true;
    }
}
