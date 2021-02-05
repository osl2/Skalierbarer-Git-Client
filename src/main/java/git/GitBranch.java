package git;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.List;

/**
 * Represents a git-branch in this program.
 */
public class GitBranch {
    private final Ref ref;

    /* Is only instantiated inside the git Package */
    GitBranch(Ref branch) {
        this.ref = branch;
    }


    /**
     * Method to get the current value of the Branch name.
     *
     * @return Name of the branch
     */
    public String getName() {
        return ref.getName().substring(ref.getName().lastIndexOf("/") + 1);
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
     * Merge this branch into another one.
     *
     * @param b           the branch to be merged into
     * @param fastforward use fast-forward?
     * @return A list of conflicting pieces of code. This list can be empty if the merge
     * is completable without user interaction
     */
    public List<GitChangeConflict> merge(GitCommit b, boolean fastforward) {
        //TODO: Implementieren!!
        return null;
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
