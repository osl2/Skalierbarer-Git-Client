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
package git.conflict;

/**
 * Represents a part of a file which has conflicting changes.
 * The hunk does not need to be part of a conflict. For parts which do not have any conflict
 * See {@link TextHunk} and for conflicting parts of the file see {@link ConflictHunk}
 */
public abstract class AbstractHunk {
    protected boolean resolved = false;

    /**
     * Returns the lines which represent the current state of the hunk
     *
     * @return Array of Lines.
     */
    public abstract String[] getLines();

    /**
     * The state of the hunk. If there is still interaction needed it will return false.
     * @return true if no further interaction is needed to resolve an apparent conflict
     */
    public boolean isResolved() {
        return resolved;
    }

    /**
     * Returns "our" side of the problem
     * @return one side of the conflict
     */
    public abstract String[] getOurs();

    /**
     * Returns "their" side of the problem
     * @return the other side of the conflict
     */
    public abstract String[] getTheirs();
}
