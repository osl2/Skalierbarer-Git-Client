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

    public ConflictHunk(String[] ours, String[] theirs) {
        this.ours = ours;
        this.theirs = theirs;
        this.result = new ArrayList<>();
    }

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

    public void acceptOurs() {
        if (acceptedOurs) return;

        Collections.addAll(result, ours);
        acceptedOurs = true;
        resolved = true;


    }

    public void acceptTheirs() {
        if (acceptedTheirs) return;

        Collections.addAll(result, theirs);
        acceptedTheirs = true;
        resolved = true;

    }

    /**
     * Returns the lines which represent the current state of the hunk
     *
     * @return Array of Lines.
     */
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
