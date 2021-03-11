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
