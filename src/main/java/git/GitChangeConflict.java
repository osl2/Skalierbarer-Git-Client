package git;

/**
 * Represents a single Change Conflict which may happen during a merge.
 */
public class GitChangeConflict {
    private GitFile gitFile;
    private int startLine;
    private int length;

    /* Is only instantiated inside the git Package */
    GitChangeConflict(GitFile gitFile, int startLine, int length) {
        this.gitFile = gitFile;
        this.startLine = startLine;
        this.length = length;
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

}
