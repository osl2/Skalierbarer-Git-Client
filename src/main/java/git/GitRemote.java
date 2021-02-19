package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class GitRemote {
    // This has to be a String as URL is a too restrictive data type refusing to operate on totally valid
    // git urls.
    private String url;
    private String gitUser;
    private String name;
    private List<GitBranch> branches;
    private LinkedList<GitBranch> fetchBranches = new LinkedList<GitBranch>();


    /* Is only instantiated inside the git Package */
    GitRemote(String url, String gitUser, String name) {
        this.url = url;
        this.gitUser = gitUser;
        this.name = name;
    }

    public void setGitUser(String gitUser) {
        this.gitUser = gitUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addBranch(GitBranch branch) {
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
    public boolean remove() throws GitException {
        Git git = GitData.getJGit();
        try {
            git.remoteRemove().setRemoteName(name).call();
            return true;
        } catch (GitAPIException e) {
            throw new GitException(e.getMessage());
        }

    }

    /**
     * Changes the Url of this Repository in JGit
     *
     * @param newUrl the new URL
     * @return true if it was succesfully
     * @throws GitException if something went wrong.
     */
    public boolean setUrlGit(String newUrl) throws GitException {
        url = newUrl;
        Git git = GitData.getJGit();
        try {
            URIish ur = new URIish(newUrl);
            git.remoteSetUrl().setRemoteName(name).setRemoteUri(ur).call();
            return true;
        } catch (GitAPIException | URISyntaxException e) {
            throw new GitException(e.getMessage());
        }
    }
}

