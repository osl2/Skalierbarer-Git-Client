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

import java.util.ArrayList;
import java.util.Collections;

/**
 * This file represents a conflict in a conflicting file.
 */
public class ConflictHunk extends AbstractHunk {
    private static final String CONFLICT_MARKER_BEGIN = "<<<<<<< ";
    private static final String CONFLICT_MARKER_SEPARATOR = "=======";
    private static final String CONFLICT_MARKER_END = ">>>>>>> ";
    private final String[] ours;
    private final String[] theirs;
    private final ArrayList<String> result;
    private boolean acceptedTheirs;
    private boolean acceptedOurs;

    /**
     * Configure the Conflict by providing both sides as text
     * @param ours one side
     * @param theirs the other side
     */
    public ConflictHunk(String[] ours, String[] theirs) {
        this.ours = ours;
        this.theirs = theirs;
        this.result = new ArrayList<>();
    }

    /**
     * Configure the Conflict by providing the file's contents and a
     * start marker
     * @param lines         the file content
     * @param startIndex    the line the opening conflict marker is on
     */
    public ConflictHunk(String[] lines, int startIndex) {
        if (!lines[startIndex].startsWith(CONFLICT_MARKER_BEGIN)) {
            throw new AssertionError("No Change at line " + startIndex);
        }
        ArrayList<String> currentSideContent = new ArrayList<>();
        int i = startIndex + 1;
        while (!lines[i].equals(CONFLICT_MARKER_SEPARATOR)) {
            currentSideContent.add(lines[i]);
            i++;
        }
        ours = currentSideContent.toArray(String[]::new);
        currentSideContent.clear();
        i++; // Skip marker
        while (!lines[i].startsWith(CONFLICT_MARKER_END)) {
            currentSideContent.add(lines[i]);
            i++;
        }
        theirs = currentSideContent.toArray(String[]::new);
        this.result = new ArrayList<>();
    }

    /**
     * accept our side of the change.
     * This appends our side to {@link #getLines()}
     */
    public void acceptOurs() {
        if (acceptedOurs) return;

        Collections.addAll(result, ours);
        acceptedOurs = true;
        resolved = true;


    }

    /**
     * accept their side of the change.
     * This appends their side to {@link #getLines()}
     */
    public void acceptTheirs() {
        if (acceptedTheirs) return;

        Collections.addAll(result, theirs);
        acceptedTheirs = true;
        resolved = true;

    }

    /**
     * Explicitly specify that NO side is  to be accepted.
     * This also clears {@link #getLines()}} but sets {@link #isResolved()} true
     */
    public void acceptNone() {
        result.clear();
        acceptedOurs = false;
        acceptedTheirs = false;
        resolved = true;
    }

    @Override
    public String[] getLines() {
        return result.toArray(String[]::new);
    }

    @Override
    public String[] getOurs() {
        return ours.clone();
    }

    @Override
    public String[] getTheirs() {
        return theirs.clone();
    }
}
