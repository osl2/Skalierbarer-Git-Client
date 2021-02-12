package git;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private int startLine;
    private int length;

    /* Is only instantiated inside the git Package */
    GitChangeConflict(GitFile gitFile, int startLine, int length, String optionA, String optionB) {
        this.gitFile = gitFile;
        this.startLine = startLine;
        this.length = length;
        this.optionA = optionA;
        this.optionB = optionB;
    }

    static List<GitChangeConflict> getConflictsForFile(GitFile file) {
        // File exists? -> Search for conflict markers -> Generate objects -> return list/array
        List<GitChangeConflict> returnMap = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            String line;
            int lineNo = 0; // 0-index to match jgit
            while ((line = br.readLine()) != null) {
                if (line.startsWith(CONFLICT_MARKER_BEGIN)) {
                    int conflictStart = lineNo;
                    String conflictStartLine = line;
                    // parse conflict
                    StringBuilder conflictA = new StringBuilder();
                    StringBuilder conflictB = new StringBuilder();
                    lineNo++;
                    // TODO: Parse which branch owns this segment
                    while ((line = br.readLine()) != null && !line.startsWith(CONFLICT_MARKER_SEPARATOR)) {
                        conflictA.append(line).append(System.lineSeparator());
                        lineNo++;
                    }
                    // skip separator
                    lineNo++;
                    while ((line = br.readLine()) != null && !line.startsWith(CONFLICT_MARKER_END)) {
                        conflictB.append(line).append(System.lineSeparator());
                        lineNo++;
                    }
                    // skip end marker
                    lineNo++;
                    returnMap.add(new GitChangeConflict(file,
                            conflictStart,
                            lineNo - conflictStart - 1,
                            conflictA.toString(),
                            conflictB.toString()
                    ));
                }
                lineNo++;
            }
        } catch (IOException e) {
            // todo error handling -> crash ._.
            e.printStackTrace();
        }

        if (returnMap.size() == 0) {
            // No Conflictmarkers -> Whole file must be a conflict. So from which branch is that file?
            // todo: implement -> Needs a way to access the filetree of an arbitrary commit
        }

        return returnMap;

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
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getResult() {
        throw new AssertionError("Not implemented");
    }
}
