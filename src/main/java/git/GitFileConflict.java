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

import git.conflict.AbstractHunk;
import git.conflict.ConflictHunk;
import git.conflict.TextHunk;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.IndexDiff;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a single Change Conflict which may happen during a merge.
 */
public class GitFileConflict {
    private static final String CONFLICT_MARKER_BEGIN = "<<<<<<< ";
    private static final String CONFLICT_MARKER_END = ">>>>>>> ";
    private final List<AbstractHunk> hunkList;
    private final GitFile gitFile;
    private final boolean deleted;

    private GitFileConflict(GitFile gitFile, List<AbstractHunk> hunkList, boolean deleted) {
        this.gitFile = gitFile;
        this.hunkList = Collections.unmodifiableList(hunkList);
        this.deleted = deleted;
    }

    /**
     * Obtain a GitFileConflict representing the conflicts in the current File.
     *
     * @param gitFile File, for which the conflicts are wanted
     * @param state   State of the conflicts
     * @return GitFileConflict representing all conflicts in the file.
     */
    public static GitFileConflict getConflictsForFile(GitFile gitFile, IndexDiff.StageState state) {
        ArrayList<AbstractHunk> hunkList = new ArrayList<>();
        switch (state) {
            case DELETED_BY_THEM:
                hunkList.add(new ConflictHunk(readFileContents(gitFile), new String[0]));
                return new GitFileConflict(gitFile, hunkList, true);
            case DELETED_BY_US:
                hunkList.add(new ConflictHunk(new String[0], readFileContents(gitFile)));
                return new GitFileConflict(gitFile, hunkList, true);
            case BOTH_ADDED:
            case BOTH_MODIFIED:
                // Multiple Conflicts possible. We need to parse in file conflict markers.
                String[] lines = readFileContents(gitFile);
                hunkList.addAll(parseConflictMarkers(lines));
                return new GitFileConflict(gitFile, hunkList, false);
            default:
                throw new AssertionError("Unexpected Change type: " + state.toString());
        }
    }

    static List<GitFileConflict> getConflictsForWorkingDirectory() throws GitAPIException {
        List<GitFileConflict> conflicts = new ArrayList<>();
        Map<String, IndexDiff.StageState> statusMap = GitData.getJGit().status().call().getConflictingStageState();

        for (Map.Entry<String, IndexDiff.StageState> entry : statusMap.entrySet()) {
            File f = new File(GitData.getRepository().getWorkTree(), entry.getKey());
            GitFile gitFile = new GitFile(f.getTotalSpace(), f);
            conflicts.add(getConflictsForFile(gitFile, entry.getValue()));
        }

        return conflicts;
    }

    private static List<AbstractHunk> parseConflictMarkers(String[] lines) {
        ArrayList<AbstractHunk> hunkList = new ArrayList<>();
        ArrayList<String> currentHunkContent = new ArrayList<>();
        int line = 0;
        while (line < lines.length) {
            if (lines[line].startsWith(CONFLICT_MARKER_BEGIN)) {
                if (!currentHunkContent.isEmpty()) {
                    hunkList.add(new TextHunk(currentHunkContent.toArray(String[]::new)));
                    currentHunkContent.clear();
                }
                hunkList.add(new ConflictHunk(lines, line));
                while (!lines[line].startsWith(CONFLICT_MARKER_END)) line++;
            } else {
                currentHunkContent.add(lines[line]);
            }
            line++;
        }
        if (!currentHunkContent.isEmpty()) {
            hunkList.add(new TextHunk(currentHunkContent.toArray(String[]::new)));
        }

        return hunkList;
    }

    private static String[] readFileContents(GitFile file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file.getPath()))) {
            return br.lines().toArray(String[]::new);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not read {}", file.getPath().getPath());
            return new String[0];
        }
    }

    /**
     * Has the file been deleted in one of the sides?
     * @return true if the file has been deleted in one of the sides
     */
    public boolean wasDeleted() {
        return deleted;
    }

    public List<AbstractHunk> getHunkList() {
        return List.copyOf(hunkList);
    }

    public List<ConflictHunk> getConflictHunkList() {
        ArrayList<ConflictHunk> conflictHunks = new ArrayList<>();
        for (AbstractHunk hunk : hunkList) {
            if (hunk instanceof ConflictHunk) {
                conflictHunks.add((ConflictHunk) hunk);
            }
        }
        return List.copyOf(conflictHunks);
    }

    public GitFile getGitFile() {
        return gitFile;
    }

    /**
     * Apply the resolution to the file system and stage changes
     *
     * @return true         if the files were successfully changed
     *         false        if at least one conflict hasn't been resolved
     * @throws IOException  if the writing failed
     * @throws GitException if the file couldn't be staged
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean apply() throws IOException, GitException {
        if (hunkList.stream().anyMatch(e -> !e.isResolved())) {
            return false; // There are still hunks to be resolved
        }

        if (deleted) {
            Files.delete(gitFile.getPath().toPath());
            gitFile.rm();
            return true;
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(gitFile.getPath()))) {
            for (AbstractHunk hunk : hunkList) {
                for (String line : hunk.getLines()) {
                    bufferedWriter.write(line);
                    bufferedWriter.write(System.lineSeparator());
                }
            }
            bufferedWriter.flush();
        }

        gitFile.add();
        return true;
    }

}
