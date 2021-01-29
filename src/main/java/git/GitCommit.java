package git;

import java.util.Date;

public class GitCommit {
    private final GitAuthor author;
    private final String message;
    private GitCommit[] parents;
    private final String hash;
    private final boolean isSigned;
    private Date date;

    /* Is only instantiated inside the git Package */
    GitCommit(GitAuthor author, String message, String hash, boolean isSigned, Date date, GitCommit[] parents) {
        this.author = author;
        this.message = message;
        this.hash = hash;
        this.parents = parents;
        this.isSigned = isSigned;
        this.date = date;
    }

    public GitAuthor getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public GitCommit[] getParents() {
        return parents;
    }

    public String getHash() {
        return hash;
    }

    public boolean isSigned() {
        return isSigned;
    }


    public boolean revert() {
        throw new AssertionError("not implemented yet");
    }

    /**
     * Generates the difference between this commit and the one passed
     *
     * @param other the other commit
     * @return String representation of the diff
     */
    public String getDiff(GitCommit other) {
        throw new AssertionError("not implemented yet");
    }

    /**
     * Generates the difference to the working directory
     *
     * @return String representation to the working directory
     */
    public String getDiff() {
        throw new AssertionError("not implemented yet");
    }

}
