package git;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Iterator;

/**
 * Class to iterate efficiently over parrent-commits.
 */
public class CommitIterator implements Iterator<GitCommit> {
    Iterator<RevCommit> revIterator;

    /**
     * Generates a CommitIterator by the RevCommit
     * @param revIterator Iterator, that should be used
     */
    public CommitIterator(Iterable<RevCommit> revIterator) {
        if (revIterator == null)
            this.revIterator = null;
        else
            this.revIterator = revIterator.iterator();
    }

    @Override
    public boolean hasNext() {
        if (revIterator == null)
            return false;
        return revIterator.hasNext();
    }

    @Override
    public GitCommit next() {
        if (revIterator == null)
            throw new UnsupportedOperationException();
        return new GitCommit(revIterator.next());
    }
}
