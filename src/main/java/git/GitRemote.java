/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a remote in the Git package.
 */
public class GitRemote {
    private final LinkedList<GitBranch> fetchBranches = new LinkedList<>();
    // This has to be a String as URL is a too restrictive data type
    // refusing to operate on totally valid git urls.
    private String url;
    private String name;
    @SuppressWarnings("unused")
    private List<GitBranch> branches;


    /* Is only instantiated inside the git Package */

    /**
     * Method to instantiate a remote by url, User and name.
     *
     * @param url  url that the remote should get
     * @param name name, that the remote has
     */
    GitRemote(String url, String name) {
        this.url = url;
        this.name = name;
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
            return (remote.getUrl().equals(this.getUrl())
                    && remote.getName().equals(this.getName())
                    && remote.getFetchBranches().equals(this.getFetchBranches()));
        }
        return false;
    }

  @Override
  public int hashCode() {
    return Objects.hash(url, name, fetchBranches);
  }
}

