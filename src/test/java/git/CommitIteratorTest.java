package git;

import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.revwalk.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class CommitIteratorTest extends AbstractGitTest{
  CommitIterator iterator;

  @Test
  void testWithNull(){
    iterator = new CommitIterator(null);
    assertFalse(iterator.hasNext());
    assertThrows(UnsupportedOperationException.class, () -> iterator.next());
  }

  @Test
  void testWithIterable() throws GitAPIException {
    Iterable<RevCommit> gitIterator = git.log().call();
    assertTrue(gitIterator.iterator().hasNext());
    iterator = new CommitIterator(gitIterator);
    assertTrue(iterator.hasNext());
  }
}
