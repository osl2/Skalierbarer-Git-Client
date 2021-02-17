package git;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class GitRemote {
    private URL url;
    private String gitUser;
    private String name;
    private List<GitBranch> branches;
    private LinkedList<GitBranch> fetchBranches = new LinkedList<GitBranch>();



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

    public String getName() {
        return name;
    }

    public URL getUrl() {
        return url;
    }
    public void addBranch(GitBranch branch){
       // if (!(fetchBranches.contains(branch))) {
            fetchBranches.add(branch);
       // }
    }

    public List<GitBranch> getFetchBranches() {
        return (List<GitBranch>) fetchBranches.clone();
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

