package git;

import java.util.Date;
import java.util.List;

public class GitStash {

    private List<GitFile> changes;
    private String id;
    private Date date;

    GitStash(List<GitFile> changes, String id, Date date) {
        this.changes = changes;
        this.id = id;
        this.date = date;
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
