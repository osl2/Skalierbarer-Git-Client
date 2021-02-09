package git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

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
     * Method to get a CanonicalTreeParser
     * TODO: JAVADOC
     *
     * @param commitId
     * @param git
     * @return
     * @throws IOException
     */
    private static AbstractTreeIterator getCanonicalTreeParser(ObjectId commitId, Git git) throws IOException {
        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit commit = walk.parseCommit(commitId);
        ObjectId treeId = commit.getTree().getId();
        try (ObjectReader reader = git.getRepository().newObjectReader()) {
            return new CanonicalTreeParser(null, reader, treeId);
        }

    }

    /**
     * Method to get the files changed in that Commit
     *
     * @return List of the changed Files
     */
    public List<GitFile> getChangedFiles() throws IOException {
        initializeCommit();
        Repository repository = GitData.getRepository();
        Git git = GitData.getJGit();
        RevWalk walk = new RevWalk(repository);
        RevCommit oldCommit = Arrays.stream(revCommit.getParents()).iterator().next();

        AbstractTreeIterator oldTreeIterator = getCanonicalTreeParser(oldCommit, git);
        AbstractTreeIterator newTreeIterator = getCanonicalTreeParser(revCommit, git);

        DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
        diffFormatter.setRepository(repository);
        List<DiffEntry> diffEntries = diffFormatter.scan(oldTreeIterator, newTreeIterator);
        List<GitFile> files = new ArrayList<>();
      String path;
      File filePath;
      int size;

      for (DiffEntry diffEntry : diffEntries) {
          path = diffEntry.getPath(DiffEntry.Side.NEW);
          filePath = new File(repository.getWorkTree(), path);
          size = (int) filePath.length();
          files.add(new GitFile(size, filePath));
      }
      return  files;
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
