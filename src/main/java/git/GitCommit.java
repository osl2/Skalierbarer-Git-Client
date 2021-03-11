package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RevertCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import settings.Settings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * The representation of a Commit in this program.
 */
public class GitCommit {
    private final RevCommit revCommit;

    /**
     * Creates a new GitCommit with the given RevCommit.
     *
     * @param revCommit given RevCommit to create the GitCommit.
     */
    GitCommit(RevCommit revCommit) {
        this.revCommit = revCommit;
    }

    /**
     * Generates the difference between the index (current HEAD position) and the working directory.
     *
     * @param file the file you want to get the git diff.
     * @return String representation of the git diff.
     * @throws IOException when the File could not be accessed
     * @throws GitException if the diff fails due to git related reasons
     */
    public static String getDiff(GitFile file) throws IOException, GitException {
        Git git = GitData.getJGit();
        AbstractTreeIterator newTreeIterator;
        TreeFilter filter = pathFilter(file);
        if (file.isStaged()) {
            newTreeIterator = new DirCacheIterator(DirCache.read(git.getRepository()));
        } else {
            newTreeIterator = new FileTreeIterator(git.getRepository());
        }
        OutputStream out = new ByteArrayOutputStream();
        try {
            GitData data = new GitData();
            AbstractTreeIterator oldTreeIterator = new EmptyTreeIterator();
            if (!data.getBranches().isEmpty()) {
                oldTreeIterator = getCanonicalTreeParser(data.getSelectedBranch().getCommit().getRevCommit(), git);
            }
            git.diff()
                    .setOldTree(oldTreeIterator)
                    .setNewTree(newTreeIterator)
                    .setPathFilter(filter)
                    .setOutputStream(out)
                    .call();
        } catch (GitAPIException | GitException e) {
            Logger.getGlobal().warning(e.getMessage());
        }
        return out.toString();
    }

    /**
     * The author of this Commit.
     *
     * @return the author.
     */
    public GitAuthor getAuthor() {
        initializeCommit();
        return new GitAuthor(
                revCommit.getAuthorIdent().getName(),
                revCommit.getAuthorIdent().getEmailAddress());
    }

    /**
     * The message of this Commit.
     * @return the Commit message.
     */
    public String getMessage() {
        initializeCommit();
        return this.revCommit.getFullMessage();
    }

    /**
     * The first line of the Commit message.
     * @return the fist line of the Commit message.
     */
    public String getShortMessage() {
        initializeCommit();
        return this.revCommit.getShortMessage();
    }

    /**
     * Return an abbreviation (prefix) with length 7 of the SHA-1.
     * This guarantees no uniqueness.
     * @return a String with length 7 of the abbreviation in lower
     * case hexadecimal.
     */
    public String getHashAbbrev() {
        initializeCommit();
        return this.revCommit.abbreviate(7).name();
    }

    /**
     * Return an array of all direct parents of this GitCommit.
     * @return an array containing all direct parents.
     */
    public GitCommit[] getParents() {
        initializeCommit();
        return Arrays.stream(this.revCommit.getParents()).map(GitCommit::new).toArray(GitCommit[]::new);
    }

    /**
     * The date this Commit was committed.
     * @return the Commit date.
     */
    public Date getDate() {
        initializeCommit();
        return Date.from(Instant.ofEpochSecond(this.revCommit.getCommitTime()));
    }

    /**
     * The string form of the SHA-1, in lower case hexadecimal.
     * @return the string form of the SHA-1, in lower case hexadecimal.
     */
    public String getHash() {
        initializeCommit();
        return this.revCommit.getName();
    }

    /**
     * Return true if the Commit is signed, else false.
     * @return true if signed, else false.
     */
    @SuppressWarnings("unused")
    public boolean isSigned() {
        initializeCommit();
        return this.revCommit.getRawGpgSignature() != null;
    }


    /**
     * Applying the git revert command on this Commit.
     *
     * @return true if this Commit was successfully reverted.
     * @throws GitException if revert can not be executed successfully.
     */
    public List<GitFileConflict> revert() throws GitException {
        Git git = GitData.getJGit();
        List<GitFileConflict> conflicts = new ArrayList<>();
        try {
            RevertCommand revertCommand = git.revert().include(revCommit);
            RevCommit rev = revertCommand.call();
            if (rev == null) { // There have been conflicts, so no commit was created
                conflicts = GitFileConflict.getConflictsForWorkingDirectory();
            }

        } catch (GitAPIException e) {
            throw new GitException("Beim Rückgängig machen des Commits ist ein Fehler aufgetreten" +
                "Fehlermeldung: " + e.getMessage());
        }
        return conflicts;
    }

    /**
     * The JGit representation of the GitCommit. This method should only be
     * visible in the git package.
     *
     * @return the RevCommit this GitCommit is based on.
     */
    RevCommit getRevCommit() {
        return this.revCommit;
    }

    private void initializeCommit() {
        if (revCommit.getRawBuffer() == null) {
            try (RevWalk revWalk = new RevWalk(GitData.getRepository())) {
                revWalk.parseHeaders(this.revCommit);
            } catch (IOException e) {
                Logger.getGlobal().warning(e.getMessage());
            }
        }
    }

    /**
     * Generates the difference between the given file this commit and the one passed.
     *
     * @param other the other commit, if you want to compare to the empty git repository set other to null.
     * @param file  the file you want to get the git diff.
     * @return String representation of the diff.
     * @throws IOException if the file couldn't be accessed.
     */
    public String getDiff(GitCommit other, GitFile file) throws IOException {
        Git git = GitData.getJGit();
        AbstractTreeIterator oldTreeIterator = new EmptyTreeIterator();
        TreeFilter filter = pathFilter(file);
        // if this commit is not the first commit of the tree.
        if(other != null) {
            RevCommit oldCommit = other.getRevCommit();
            oldTreeIterator = getCanonicalTreeParser(oldCommit, git);
        }
        AbstractTreeIterator newTreeIterator = getCanonicalTreeParser(revCommit, git);
        OutputStream out = new ByteArrayOutputStream();
        try {
            git.diff()
                    .setOldTree(oldTreeIterator)
                    .setNewTree(newTreeIterator)
                    .setPathFilter(filter)
                    .setOutputStream(out)
                    .call();
        } catch (GitAPIException e) {
            Logger.getGlobal().warning(e.getMessage());
        }
        return out.toString();
    }

    private static TreeFilter pathFilter(GitFile file) {
        String separator = Pattern.quote(System.getProperty("file.separator"));
        String[] relativePath = file.getPath().getPath().split(separator);
        StringBuilder output = new StringBuilder(relativePath[0]);
        for(int i = 1; i < relativePath.length; i++) {
            output.append("/").append(relativePath[i]);
        }
        output = new StringBuilder(output.substring(Settings.getInstance().getActiveRepositoryPath().getPath().length() + 1));
        return PathFilter.create(output.toString());
    }



    private static AbstractTreeIterator getCanonicalTreeParser(ObjectId commitId, Git git) throws IOException {
        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit commit = walk.parseCommit(commitId);
        ObjectId treeId = commit.getTree().getId();
        try (ObjectReader reader = git.getRepository().newObjectReader()) {
            return new CanonicalTreeParser(null, reader, treeId);
        }

    }

    /**
     * Method to get the files changed in that Commit.
     *
     * @return List of the changed Files.
     */
    public List<GitFile> getChangedFiles() throws IOException {
        initializeCommit();
        Repository repository = GitData.getRepository();
        Git git = GitData.getJGit();
        AbstractTreeIterator oldTreeIterator = new EmptyTreeIterator();
        if(revCommit.getParents().length!= 0) {
            RevCommit oldCommit = Arrays.stream(revCommit.getParents()).iterator().next();
            oldTreeIterator = getCanonicalTreeParser(oldCommit, git);
        }

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
          // A simple way to get deleted files.
          if(path.compareTo("/dev/null") == 0) {
              path = diffEntry.getPath(DiffEntry.Side.OLD);
          }
          filePath = new File(repository.getWorkTree(), path);
          size = (int) filePath.length();
          files.add(new GitFile(size, filePath));
      }
      return  files;
  }

    /**
     * Returns true if the string form of the SHA-1 from this GitCommit, in lower case hexadecimal
     * equals the string form of the SHA-1, in lower case hexadecimal of the
     * given GitCommit. If the given object is no GitCommit return false.
     * @param o the object to compare to.
     * @return true if the two objects are equal else false
     */
  @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GitCommit)) return false;
        GitCommit gitCommit = (GitCommit) o;
        return revCommit.getName().equals(gitCommit.revCommit.getName());
    }

    /**
     * Return a hash code representation of this object.
     * @return a hash representation of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(revCommit);
    }
}
