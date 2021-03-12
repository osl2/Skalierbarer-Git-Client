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
 * This file is used to represent unchanged parts of a file including conflicts.
 */
public class TextHunk extends AbstractHunk {
    private final String[] lines;

    /**
     * Configure this hunk by providing the lines without conflict
     * @param lines the continuos array of lines to represent
     */
    public TextHunk(String[] lines) {
        this.lines = lines;
        resolved = true;
    }

    @Override
    public String[] getLines() {
        return lines;
    }

    @Override
    public String[] getOurs() {
        return getLines();
    }

    @Override
    public String[] getTheirs() {
        return getLines();
    }
}
