package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a remote in the Git package.
 */
public class GitRemote {
    private final LinkedList<GitBranch> fetchBranches = new LinkedList<>();
    // This has to be a String as URL is a too restrictive data type
    // refusing to operate on totally valid git urls.
    private String url;
    @SuppressWarnings("unused")
    private String gitUser;
    private String name;
    @SuppressWarnings("unused")
    private List<GitBranch> branches;


    /* Is only instantiated inside the git Package */

    /**
     * Method to instantiate a remote by url, User and name.
     *
     * @param url     url that the remote should get
     * @param gitUser user, that the remote has
     * @param name    name, that the remote has
     */
    GitRemote(String url, String gitUser, String name) {
        this.url = url;
        this.gitUser = gitUser;
        this.name = name;
    }

    /**
     * Method to get the user of the remote.
     *
     * @param gitUser User of the remote
     */
    @SuppressWarnings("unused")
    public void setGitUser(String gitUser) {
        this.gitUser = gitUser;
    }

    /**
     * Method to get the name of the remote.
     *
     * @return name of the repository
     */
    public String getName() {
        return name;
    }

    /**
     * Method to set the name of a repository.
     *
     * @param name Name that the remote should get
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method to get the URL of the remote.
     *
     * @return URL of the remote
     */
    public String getUrl() {
        return url;
    }

    /**
     * Method to add a branch to the remote.
     *
     * @param branch branch that should be added
     */
    public void addBranch(GitBranch branch) {
        fetchBranches.add(branch);
    }

    /**
     * Method to get all branches, that can be fetched.
     *
     * @return All branches that can be fetched
     */
    @SuppressWarnings("unchecked")
    public List<GitBranch> getFetchBranches() {
        Object cloned = fetchBranches.clone();
        return (List<GitBranch>) cloned;
    }

    /**
     * Removes the remote from the underlying repository.
     *
     * @return true         iff successful
     * @throws GitException if the remote couldnt be removed
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
     * Changes the Url of this Repository in JGit.
     *
     * @param newUrl the new URL
     * @return true if it was successfully
     * @throws GitException if something went wrong.
     */
    public boolean setUrl(String newUrl) throws GitException {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && getClass() == o.getClass()) {
            GitRemote remote = (GitRemote) o;
            return (remote.gitUser.equals(this.gitUser)
                    && remote.getUrl().equals(this.getUrl())
                    && remote.getName().equals(this.getName())
                    && remote.getFetchBranches().equals(this.getFetchBranches()));
        }
        return false;
    }
}

