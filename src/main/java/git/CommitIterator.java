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
