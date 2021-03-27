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
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;
import settings.Settings;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A class to do operations, that change something in the Git repository.
 */
public class GitFacade {
    private static final String ERROR_MESSAGE = "Fehlermeldung: ";

    /**
     * Checkout an other branch. It loads the data of that branch and provides the data from JGit.
     *
     * @param branch        branch that should be checked out
     * @return              true if it is successfully checked out, false if something went wrong
     * @throws GitException if checkout failed due to git
     */
    public boolean checkout(GitBranch branch) throws GitException {
        try {
            GitData.getJGit().checkout().setCreateBranch(false).setName(branch.getFullName()).call();
            return true;
        } catch (GitAPIException e) {
            throw new GitException(e.getMessage());
        }
    }

    /**
     * Checkout a commit. It loads the data of the commit and provides the data from JGit
     *
     * @param commit commit that should be checked out
     * @return true if it is performed successfully, false if something went wrong
     * @throws GitException if checkout failed due to git
     */
    public boolean checkout(GitCommit commit) throws GitException {
        try {
            GitData.getJGit().checkout().setCreateBranch(false).setName(commit.getHash()).call();
            return true;
        } catch (GitAPIException e) {
            throw new GitException(e.getMessage());
        }
    }

    /**
     * Creates a new branch with the specific name at the commit in JGit. The new branch is checked out afterward
     *
     * @param commit        commit where the new branch begins
     * @param name          name of the branch
     * @return              true if it is performed successfully, false if something went wrong
     * @throws GitException if branching wasn't possible due to git internal problems
     */
    public boolean branchOperation(GitCommit commit, String name) throws GitException {
       if (name.contains("?")){
           throw new GitException("Das Symbol ? ist in Zweignamen nicht erlaubt.");
       } else  if (name.contains(" ")){
            throw new GitException("Der Name des neuen Branches darf keine Leerzeichen enthalten");
        }
        try {
            Git git = GitData.getJGit();
            git.checkout().setCreateBranch(true).setName(name).setStartPoint(commit.getHash()).call();
            return true;
        } catch (GitAPIException e) {
            throw new GitException("Fehler beim Erstellen des neuen Branches \n"
                    + ERROR_MESSAGE + e.getMessage());
        }
    }

    /**
     * Pulls the files and commits from the branch of the remote to the local repo in Jgit.
     *
     * @param remote       remote where the commits come from
     * @param remoteBranch chosen branch where the commits originate from
     * @return true if it is performed successfully, false if something went wrong
     */
    @SuppressWarnings("unused")
    public boolean pullOperation(GitRemote remote, GitBranch remoteBranch) {
        throw new AssertionError("not implemented ");
    }

    /**
     * Creates a new Remote in JGit.
     *
     * @param name name of the new remote
     * @param url  Url of the repository
     * @return true if it is performed successfully, false if something went wrong
     * @throws GitException if the remote could not be added due to uri syntax or git.
     */
    public boolean remoteAddOperation(String name, String url) throws GitException {
        Git git = GitData.getJGit();
        try {
            git.remoteAdd().setName(name).setUri(new URIish(url)).call();
            return true;
        } catch (GitAPIException | URISyntaxException e) {
            throw new GitException(e.getMessage());
        }
    }

    /**
     * Method to initialize a new git Repositorry on a given path.
     *
     * @param path path, that is needed
     * @return true, if it is done successfully false else
     */
    public boolean initializeRepository(File path) {

        try {
            Git.init().setDirectory(path).call();
            Settings settings = Settings.getInstance();
            settings.setActiveRepositoryPath(path);
            GitData data = new GitData();
            data.reinitialize();
        } catch (GitAPIException e) {
            return false;
        }
        return true;
    }

    /**
     * Clones a repository into a directory
     * @param gitUrl the url where the origin is located
     * @param dest the directory to clone into
     * @param recursive checkout all submodules
     * @throws GitException if the clone fails due to git related reasons
     */
    public void cloneRepository(String gitUrl, File dest, boolean recursive) throws GitException {
        try {
            Git result = Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(dest)
                    .setCloneAllBranches(true)
                    .setTransportConfigCallback(CredentialProviderHolder::configureTransport)
                    .setCloneSubmodules(recursive)
                    .call();
          // Otherwise this might lead to unexpected behaviour.
          result.getRepository().close();
          result.close();
        } catch (TransportException e) {
            throw new GitException("");
        } catch (InvalidRemoteException e) {
            throw new GitException("Es wurde keine korrekte git url angegeben: " + e.getMessage());
        } catch (GitAPIException | JGitInternalException e) {
            throw new GitException("Folgender Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    /**
     * Fetches specified branches of a list of remotes. See {@link GitRemote#getFetchBranches()}
     * @param remotes           list of remotes to fetch from
     * @throws GitException     if the fetching fails due to git
     */
    public void fetchRemotes(List<GitRemote> remotes) throws GitException {
        try {
            Git jgit = GitData.getJGit();
            for (GitRemote gitRemote : remotes) {
                if (gitRemote.getFetchBranches().isEmpty()) {
                    jgit.fetch()
                            .setRemote(gitRemote.getName())
                            .setRefSpecs("refs/heads/*:refs/remotes/"
                                    + gitRemote.getName() + "/*")
                            .setTransportConfigCallback(CredentialProviderHolder::configureTransport)
                            .call();
                } else {
                    for (int j = 0; j < gitRemote.getFetchBranches().size(); j++) {
                        GitBranch actualBranch = gitRemote.getFetchBranches().get(j);
                        jgit.fetch()
                                .setRemote(gitRemote.getName())
                                .setRefSpecs("refs/heads/" + actualBranch.getName() + ":refs/remotes/" + gitRemote.getName() + "/"
                                        + actualBranch.getName())
                                .setTransportConfigCallback(CredentialProviderHolder::configureTransport)

                                .call();
                    }
                }
            }

        } catch (GitAPIException e) {
            throw new GitException(e.getMessage());
        }
    }

    /**
     * Sets the active repository path. See {@link Settings#setActiveRepositoryPath(File)}
     *
     * If the directory is not an active git repository, one will be initialized.
     *
     * @param path the path to the directory to open
     */
    public void setRepositoryPath(File path) {
        Settings settings = Settings.getInstance();
        GitData data = new GitData();
        if (RepositoryCache.FileKey.isGitRepository(path, FS.DETECTED)) {
            settings.setActiveRepositoryPath(path);
        } else {
            initializeRepository(path);
        }
        data.reinitialize();
    }

    /**
     * Commits the files in the staging-area to the git repo and adds the given commit message.
     * If amend is set to true,
     * the last commit is simply amended with the currently added files and the new message
     *
     * @param commitMessage The commit message specified by the user
     * @param amend         true if the last commit should be amended, false otherwise
     * @return True if the commit was successful
     * @throws GitException if committing failed due to unexpected git problems.
     */
    public boolean commitOperation(String commitMessage, boolean amend) throws GitException {
        try {
            Settings settings = Settings.getInstance();
            GitAuthor author = settings.getUser();
            Git jgit = GitData.getJGit();
            jgit.commit().setMessage(commitMessage).setAuthor(author.getName(), author.getEmail())
                    .setSign(false) // Currently we have no provider support to allow signatures. Therefore we force them to false
                    // See Issue !17
                    .setAmend(amend).call();

            jgit.getRepository().writeCommitEditMsg(null);
            return true;
        } catch (GitAPIException e) {
            throw new GitException("Mit dem Commit ist etwas schief gelaufen: \n " +
                    ERROR_MESSAGE + e.getMessage());
        } catch (IOException e) {
            Logger.getGlobal().warning("Couldn't delete .git/COMMIT_EDITMSG");
            // only triggered by .writeCommitEditMsg() the commit was still successful.
            return true;
        }
    }

    /**
     * Pushes the local commit history from the selected branch to the selected online repo in the selected branch. Sets up
     * the local branch to track the remote branch if 'setUpstream' is true
     *
     * @param remote            The remote repo the local changes should be pushed to
     * @param localBranch       The local branch whose changes should be pushed
     * @param remoteBranchName  The remote branch the changes should be pushed to (already existing)
     * @return True if the push has been executed successfully, false otherwise, e.g. connection to the online repo failed
     * @throws GitException     if pushing failed either due to transport problems or internal git problems.
     */
    public boolean pushOperation(GitRemote remote, GitBranch localBranch, String remoteBranchName) throws GitException {
        try {
            String url = remote.getUrl();
            Git git = GitData.getJGit();
            Repository repository = GitData.getRepository();
            Set<String> remoteNames = repository.getRemoteNames();
            if (!remoteNames.contains(remote.getName())) {
                git.remoteAdd().setName(remote.getName()).setUri(new URIish(url)).call();
            }

            git.push()
              .setRemote(remote.getName())  //In JGIT uri or name of the remote can be set
              .setTransportConfigCallback(CredentialProviderHolder::configureTransport)
                    .setRefSpecs(new RefSpec(localBranch.getName() + ":" + remoteBranchName))
                    .call();
            return true;
        } catch (InvalidRemoteException e) {
            throw new GitException("Remote war ungültig \n" +
                    ERROR_MESSAGE + e.getMessage());
        } catch (TransportException e) {
            throw new GitException("Mit der Internet-Verbindung ist etwas schief gelaufen \n" +
                    ERROR_MESSAGE + e.getMessage());
        } catch (GitAPIException | URISyntaxException e) {
            throw new GitException("Ein Fehler ist aufgetreten \n" +
                    ERROR_MESSAGE + e.getMessage());
        }
    }


    /**
     * Set a config value in the git config.
     *
     * <p>Currently only supports 2-layer deep settings like `commit.gpgsign`</p>
     * @param configOption the option to set
     * @param configValue the value to set
     * @return true if the config was set successfully
     */
    public boolean setConfigValue(String configOption, String configValue) {
        String[] option = configOption.split("\\.");
        StoredConfig config = GitData.getRepository().getConfig();
        // todo: handle options which are not 2 layers deep
        config.setString(option[0], null, option[1], configValue);
        try {
            config.save();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
