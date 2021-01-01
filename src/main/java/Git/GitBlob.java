package Git;

import java.io.File;

public class GitBlob {
    private int size;
    private File path;
    /* TODO: SOME WAY TO TRACK CHANGES */

    //for Gitignore.java
    public boolean isIgnored() {
        return false;
    }

    public boolean isVersioned() {
        return false;
    }

    /* Is only instantiated inside the Git Package */
    protected GitBlob() {
    }
}
