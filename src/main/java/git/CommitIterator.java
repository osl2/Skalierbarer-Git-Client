package git;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Iterator;

public class CommitIterator implements Iterator<GitCommit> {
    Iterator<RevCommit> revIterator;

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
