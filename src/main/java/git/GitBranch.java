package git;

import git.exception.GitException;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    public Map<GitFile, List<GitChangeConflict>> merge(boolean fastForward) throws GitException {
        MergeCommand.FastForwardMode ffm = fastForward ? MergeCommand.FastForwardMode.FF : MergeCommand.FastForwardMode.NO_FF;

        try {
            MergeResult mr = GitData.getJGit().merge()
                    .setStrategy(MergeStrategy.RESOLVE)
                    .include(ref)
                    .setFastForward(ffm)
                    .call();
            // let's reject what Jgit is doing, and just take the files it lists us, and do our own parsing
            // As the getConflicts method seems to be bugged. (2021-02-12)
            Map<GitFile, List<GitChangeConflict>> conflictMap = new HashMap<>();
            Map<String, IndexDiff.StageState> statusMap = GitData.getJGit().status().call().getConflictingStageState();

            for (Map.Entry<String, IndexDiff.StageState> entry : statusMap.entrySet()) {
                File f = new File(GitData.getRepository().getWorkTree(), entry.getKey());
                GitFile gitFile = new GitFile(f.getTotalSpace(), f);
                conflictMap.put(gitFile, GitChangeConflict.getConflictsForFile(gitFile, entry.getValue()));
            }

            return conflictMap;

        } catch (GitAPIException e) {
            throw new GitException(e.getMessage());
        }

        //return null;
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
