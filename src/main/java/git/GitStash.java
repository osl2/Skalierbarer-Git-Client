package git;

import commands.Diff;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitStash {

    private final List<GitFile> changes;
    private final String id;
    private final Date date;

    GitStash(List<GitFile> changes, String id, Date date) {
        this.changes = changes;
        this.id = id;
        this.date = date;
    }

    GitStash(RevCommit revCommit) {
        RevCommit[] parents = revCommit.getParents();
        RevCommit parent = parents [0];
        this.id = revCommit.getName();
        int stashTime = revCommit.getCommitTime();
        Instant instant = Instant.ofEpochSecond(stashTime);
        this.date = Date.from(instant);
        List<GitFile> changes = new ArrayList<>();
        this.changes = changes;
        DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
        try {
            diffFormatter.format(revCommit, parent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GitFile> getChanges() {
        return changes;
    }

    public String getId() {
        return id;
    }

    /**
     * Restore stashed data to working directory
     *
     * @return true iff successful
     */
    public boolean apply() {
        return false;
    }


}
