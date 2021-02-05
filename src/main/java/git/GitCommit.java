package git;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GitCommit {
    private final RevCommit revCommit;

    GitCommit(RevCommit revCommit) {
        this.revCommit = revCommit;
    }

    private void initializeCommit() {
        if (revCommit.getRawBuffer() == null) {
            RevWalk revWalk = new RevWalk(GitData.getRepository());
            try {
                revWalk.parseHeaders(this.revCommit);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                revWalk.dispose();
            }
        }
    }

    public GitAuthor getAuthor() {
        initializeCommit();
        return new GitAuthor(
                revCommit.getAuthorIdent().getName(),
                revCommit.getAuthorIdent().getEmailAddress());
    }

    public String getMessage() {
        initializeCommit();
        return this.revCommit.getFullMessage();
    }

    public String getShortMessage() {
        initializeCommit();
        return this.revCommit.getShortMessage();
    }

    public String getHashAbbrev() {
        initializeCommit();
        return this.revCommit.abbreviate(7).name();
    }

    public GitCommit[] getParents() {
        initializeCommit();
        return Arrays.stream(this.revCommit.getParents()).map(GitCommit::new).toArray(GitCommit[]::new);
    }

    public Date getDate() {
        initializeCommit();
        return Date.from(Instant.ofEpochSecond(this.revCommit.getCommitTime()));
    }

    public String getHash() {
        initializeCommit();
        return this.revCommit.getName();
    }

    public boolean isSigned() {
        initializeCommit();
        return this.revCommit.getRawGpgSignature() != null;
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


    /**
     * Method to get the files changed in that Commit
     *
     * @return List of the changed Files
     */
    public List<GitFile> getChangedFiles() {
        throw new AssertionError("not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GitCommit)) return false;
        GitCommit gitCommit = (GitCommit) o;
        return revCommit.getName().equals(gitCommit.revCommit.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(revCommit);
    }
}
