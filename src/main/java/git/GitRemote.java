package git;

import java.net.URL;
import java.util.List;

public class GitRemote {
    private URL url;
    private String gitUser;
    private String name;
    private List<GitBranch> branches;


    /* Is only instantiated inside the git Package */
    GitRemote(URL url, String gitUser, String name) {
        this.url = url;
        this.gitUser = gitUser;
        this.name = name;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setGitUser(String gitUser) {
        this.gitUser = gitUser;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Removes the remote from the underlying repository
     *
     * @return true iff successful
     */
    public boolean remove() {
        return false;
    }
}
