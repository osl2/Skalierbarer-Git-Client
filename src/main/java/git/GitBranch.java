package git;

import git.exception.GitException;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Represents a git-branch in this program.
 */
public class GitBranch {
    private final Ref ref;

    /* Is only instantiated inside the git Package */
    GitBranch(Ref branch) {
        this.ref = branch;
    }

    public Iterator<GitCommit> getCommits() throws GitException, IOException {
        return new GitData().getCommits(this);
    }

    /**
     * Method to get the current value of the Branch name.
     *
     * @return Name of the branch
     */
    public String getName() {
        if (ref.getName().startsWith("refs/heads/"))
            return ref.getName().substring("refs/heads/".length());
        else if (ref.getName().startsWith("refs/remotes/")){
            String[] refParts = ref.getName().split("/");
            return ref.getName().substring("refs/remotes/".length() + refParts[2].length() + 1);
        }
        else
            return ref.getName();
    }

    /**
     * Method to get the current value of the full reference name of a Branch.
     * i.e. master -> refs/heads/master
     *
     * @return Name of the branch
     */
    public String getFullName() {
        return ref.getName();
    }

    /**
     * Method to get the head of the commit.
     *
     * @return Commit, which is the head of the branch
     */
    public GitCommit getCommit() {
        RevWalk revWalk = new RevWalk(GitData.getRepository());
        GitCommit head = null;
        try {
            // dirty way to update the pointer
            Ref r = GitData.getRepository().exactRef(ref.getName());
            RevCommit revCommit = revWalk.parseCommit(r.getObjectId());
            head = new GitCommit(revCommit);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            revWalk.dispose();
        }
        return head;
    }

    /**
     * Merge this branch into the current HEAD.
     *
     * @param fastForward use fast-forward?
     * @return A list of conflicting pieces of code. This list can be empty if the merge
     * is completable without user interaction
     */
    public List<GitChangeConflict> merge(boolean fastForward) throws GitException, IOException {
        MergeCommand.FastForwardMode ffm = fastForward ? MergeCommand.FastForwardMode.FF : MergeCommand.FastForwardMode.NO_FF;

        try {
            MergeResult mr = GitData.getJGit().merge()
                    .setStrategy(MergeStrategy.RESOLVE)
                    .include(ref)
                    .setFastForward(ffm)
                    .call();

            Map<String, int[][]> conflicts = mr.getConflicts();
            // todo: [0,0,0] means file has been deleted in either branch. what am i supposed to do?
            List<GitChangeConflict> conflictList = new ArrayList<>();

            Map<String, int[][]> allConflicts = mr.getConflicts();
            for (String path : allConflicts.keySet()) {
                int[][] c = allConflicts.get(path);
                System.out.println("Conflicts in file " + path);
                for (int i = 0; i < c.length; ++i) {
                    System.out.println("  Conflict #" + i);
                    for (int j = 0; j < (c[i].length) - 1; ++j) {
                        if (c[i][j] >= 0)
                            System.out.println("    Chunk for "
                                    + mr.getMergedCommits()[j] + " starts on line #"
                                    + c[i][j]);
                    }
                }
            }

            for (String key : conflicts.keySet()) {
                File f = new File(GitData.getRepository().getWorkTree(), key);
                GitFile file = new GitFile(f.getTotalSpace(), f);
                Arrays.stream(conflicts.get(key)).map(i -> parseConflict(file, i)).forEach(conflictList::add);

            }
            return conflictList;

        } catch (GitAPIException e) {
            throw new GitException(e.getMessage());
        }

        //return null;
    }

    private GitChangeConflict parseConflict(GitFile f, int[] intArray) {
        return new GitChangeConflict(f, Integer.max(intArray[0], intArray[1]), intArray[2]);
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            GitBranch toCheck = (GitBranch) o;
            return toCheck.getCommit().equals(this.getCommit()) && toCheck.getFullName().equals(this.getFullName());
        }
        return false;
    }
}
