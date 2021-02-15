package git;

import org.eclipse.jgit.lib.IndexDiff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single Change Conflict which may happen during a merge.
 */
public class GitChangeConflict {
    private static final String CONFLICT_MARKER_BEGIN = "<<<<<<< ";
    private static final String CONFLICT_MARKER_SEPARATOR = "=======";
    private static final String CONFLICT_MARKER_END = ">>>>>>> ";
    private String optionA;
    private String optionB;
    private GitFile gitFile;
    private IndexDiff.StageState state;
    private int startLine;
    private int length;

    /* Is only instantiated inside the git Package */
    GitChangeConflict(GitFile gitFile, IndexDiff.StageState state) {
        this.gitFile = gitFile;
        this.state = state;
    }

    GitChangeConflict(GitFile gitFile, IndexDiff.StageState state, int startLine) {
        this(gitFile, state);
        this.startLine = startLine;
        populateOptions(startLine);

    }

    public static List<GitChangeConflict> getConflictsForFile(GitFile gitFile, IndexDiff.StageState state) {
        ArrayList<GitChangeConflict> returnList = new ArrayList<>();
        switch (state) {
            case BOTH_ADDED:
                // Fall through
            case DELETED_BY_THEM:
                // Fall through
            case DELETED_BY_US:
                // There is at most one conflict for that file.
                returnList.add(new GitChangeConflict(gitFile, state));
                break;
            case BOTH_MODIFIED:
                // Multiple Conflicts possible. We need to parse in file conflict markers.
                File f = gitFile.getPath();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    int i = 0;
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith(CONFLICT_MARKER_BEGIN))
                            returnList.add(new GitChangeConflict(gitFile, state, i));
                        ++i;
                    }
                    break;
                } catch (IOException e) {
                    throw new AssertionError("File in GitFile did not exist");
                }
            default:
                throw new AssertionError("Unexpected Change type: " + state.toString());
        }

        return returnList;
    }

    private void populateOptions(int startIndex) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(gitFile.getPath()));
            int i = 0;
            while (i < startIndex) {
                br.readLine();
                ++i;
            }
            // Start of Conflict
            if (!br.readLine().startsWith(CONFLICT_MARKER_BEGIN)) {
                // There was no conflict here
                throw new AssertionError("There was no change marker in File "
                        + gitFile.getPath().toString() + " at line " + startIndex);
            }
            ++i;

            String line;
            StringBuilder optionA = new StringBuilder();
            StringBuilder optionB = new StringBuilder();
            while (!Objects.equals(line = br.readLine(), CONFLICT_MARKER_SEPARATOR)) {
                optionA.append(line);
                optionA.append(System.lineSeparator());
                ++i;
            }
            ++i;
            // After separator
            while (!(line = br.readLine()).startsWith(CONFLICT_MARKER_END)) {
                optionB.append(line);
                optionB.append(System.lineSeparator());
                ++i;
            }
            // todo : Crashes if string is empty.
            if (optionA.length() > 0)
                optionA.deleteCharAt(optionA.lastIndexOf(System.lineSeparator()));
            if (optionB.length() > 0)
                optionB.deleteCharAt(optionB.lastIndexOf(System.lineSeparator()));
            this.optionA = optionA.toString();
            this.optionB = optionB.toString();
            this.length = i - startIndex - 1;


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * accepts the first option
     */
    public void acceptA() {
    }

    /**
     * accepts the second option
     */
    public void acceptB() {
    }

    public String getOptionA() {
        if (this.state == IndexDiff.StageState.DELETED_BY_US)
            return "DELETED";
        return optionA;
    }

    public String getOptionB() {
        if (this.state == IndexDiff.StageState.DELETED_BY_THEM)
            return "DELETED";
        return optionB;
    }

    public int getConflictStartLine() {
        if (this.state != IndexDiff.StageState.BOTH_MODIFIED)
            return 0;
        return this.startLine;
    }

    public int getLength() {
        return length;
    }

    public String getResult() {
        throw new AssertionError("Not implemented");
    }

}
