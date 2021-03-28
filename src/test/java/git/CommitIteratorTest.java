/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package git;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class CommitIteratorTest extends AbstractGitTest {
  CommitIterator iterator;

  @Test
  void testWithNull() {
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
