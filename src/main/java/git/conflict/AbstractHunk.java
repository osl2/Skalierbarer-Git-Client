package git.conflict;

public abstract class AbstractHunk {
    protected boolean resolved = false;

    /**
     * Returns the lines which represent the current state of the hunk
     *
     * @return Array of Lines.
     */
    public abstract String[] getLines();

    public boolean isResolved() {
        return resolved;
    }

    public boolean toBeDeleted() {
        return false;
    }

    public abstract String[] getOurs();

    public abstract String[] getTheirs();
}
