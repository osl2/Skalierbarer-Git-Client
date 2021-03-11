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
