package git;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Iterator;

public class CommitIterator implements Iterator<GitCommit> {
    Iterator<RevCommit> revIterator;

    public CommitIterator(Iterable<RevCommit> revIterator) {
        this.revIterator = revIterator.iterator();
    }

    @Override
    public boolean hasNext() {
        return revIterator.hasNext();
    }

    @Override
    public GitCommit next() {
        return new GitCommit(revIterator.next());
    }
}
