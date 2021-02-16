package git;

import org.eclipse.jgit.lib.IndexDiff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a single Change Conflict which may happen during a merge.
 */
public class GitChangeConflict {
    private static final String CONFLICT_MARKER_BEGIN = "<<<<<<< ";
    private static final String CONFLICT_MARKER_SEPARATOR = "=======";
    private static final String CONFLICT_MARKER_END = ">>>>>>> ";
    private final IndexDiff.StageState state;
    private String optionOurs;
    private GitFile gitFile;
    private String optionTheirs;
    private int startLine;
    private boolean resolved = false;
    private String result;
    private int length;

    /* Is only instantiated inside the git Package */
    GitChangeConflict(GitFile gitFile, IndexDiff.StageState state) {
        this(gitFile, state, 0);
    }

    GitChangeConflict(GitFile gitFile, IndexDiff.StageState state, int startLine) {
        this.gitFile = gitFile;
        this.state = state;
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

            if (state == IndexDiff.StageState.DELETED_BY_THEM) {
                this.optionOurs = br.lines().collect(Collectors.joining(System.lineSeparator()));
                this.optionTheirs = "DELETED";
                this.length = -1;
                return;
            } else if (state == IndexDiff.StageState.DELETED_BY_US) {
                this.optionTheirs = br.lines().collect(Collectors.joining(System.lineSeparator()));
                this.optionOurs = "DELETED";
                this.length = -1;
                return;
            }
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
            StringBuilder optionOursBuilder = new StringBuilder();
            StringBuilder optionTheirsBuilder = new StringBuilder();
            while (!Objects.equals(line = br.readLine(), CONFLICT_MARKER_SEPARATOR)) {
                optionOursBuilder.append(line);
                optionOursBuilder.append(System.lineSeparator());
                ++i;
            }
            ++i;
            // After separator
            while (!(line = br.readLine()).startsWith(CONFLICT_MARKER_END)) {
                optionTheirsBuilder.append(line);
                optionTheirsBuilder.append(System.lineSeparator());
                ++i;
            }
            // todo : Crashes if string is empty.
            if (optionOursBuilder.length() > 0)
                optionOursBuilder.deleteCharAt(optionOursBuilder.lastIndexOf(System.lineSeparator()));
            if (optionTheirsBuilder.length() > 0)
                optionTheirsBuilder.deleteCharAt(optionTheirsBuilder.lastIndexOf(System.lineSeparator()));
            this.optionOurs = optionOursBuilder.toString();
            this.optionTheirs = optionTheirsBuilder.toString();
            this.length = i - startIndex - 1;


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * accepts the first option
     */
    public void accecptOurs() {
        if (resolved) return;
        if (result == null) result = "";
        this.result += getOptionOurs() + System.lineSeparator();
    }

    /**
     * accepts the second option
     */
    public void acceptTheirs() {
        if (resolved) return;
        if (result == null) result = "";
        this.result += getOptionTheirs() + System.lineSeparator();

    }

    public String getOptionOurs() {
        return optionOurs;
    }

    public String getOptionTheirs() {
        return optionTheirs;
    }

    public int getConflictStartLine() {
        if (this.state != IndexDiff.StageState.BOTH_MODIFIED)
            return 0;
        return this.startLine;
    }

    /**
     * Get the length of the conflict. May return -1 if the whole file was deleted in one of the conflicting commits.
     *
     * @return length of the conflict in the file
     */
    public int getLength() {
        return length;
    }

    /**
     * may return null if {@link #isResolved()} returns false
     *
     * @return String representation of the current solution
     */
    public String getResult() {
        if (resolved && result == null)
            return "" + System.lineSeparator();

        return result;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void resolve() {
        this.resolved = true;
    }
}
