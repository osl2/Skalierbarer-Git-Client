package git;

import git.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.StashListCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import settings.Settings;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Provides a central point to obtain and create a number of git objects.
 */
public class GitData {

    private static org.eclipse.jgit.api.Git git;
    private static Repository repository;

    public GitData() throws GitException {
        if (git == null || repository == null) {
            initializeRepository();
        }
    }

    static Repository getRepository() {
        return repository;
    }

    static Git getJGit() {
        return git;
    }

    public void reinitialize() throws GitException {
        initializeRepository();
    }

    private void initializeRepository() throws GitException {
        Settings settings = Settings.getInstance();
        File path = settings.getActiveRepositoryPath();
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            builder.setMustExist(true);
            if (path.getAbsolutePath().matches(".*/.git$"))
                builder.setGitDir(path);
            else
                builder.setWorkTree(path);
            //builder.setGitDir(path);
            repository = builder.build();
            git = Git.open(path);
        } catch (IOException e) {
            throw new GitException("Beim Öffnen des Git-Repositorrys ist etwas schief gelaufen " +
                "Fehlermeldung: " + e.getMessage());
        }
    }

    public GitBranch getSelectedBranch() throws IOException {
        String branchName = git.getRepository().getFullBranch();
        return new GitBranch(repository.exactRef(branchName));
    }

    /**
     * Get all commits of the current Repository.
     *
     * @return Array of all commits without stashes
     */
    public Iterator<GitCommit> getCommits() throws IOException, GitException {
        try {
            Iterable<RevCommit> allCommits;
            allCommits = git.log().all().call();
            return new CommitIterator(allCommits);
        }catch (NoHeadException e) {
            throw new GitException("Der Head wurde nicht gefunden" +
                "\n Fehlermeldung: " + e.getMessage());
        } catch (GitAPIException e) {
            throw new GitException("Eine nicht genauer spezifizierte Fehlermeldung in Git ist aufgetreten \n" +
                "Fehlermeldung: " + e.getMessage());
        }
    }

    /**
     * Get a page of commits for a branch
     *
     * @param branch the Branch to get commits for
     * @return Array of all commits without stashes
     */
    Iterator<GitCommit> getCommits(GitBranch branch) throws GitException, IOException {
        try {
            Iterable<RevCommit> allCommits;
            allCommits = git.log().add(repository.resolve(branch.getFullName()))
                    .call();
            return new CommitIterator(allCommits);
        } catch (NoHeadException e) {
            throw new GitException("Der Head wurde nicht gefunden \n" +
                "Fehlermeldung: " + e.getMessage());
        } catch (IncorrectObjectTypeException e) {
            throw new GitException("Mit Git ist etwas Schiefgelaufen \n" +
                "Fehlermeldung: " + e.getMessage());
        } catch (AmbiguousObjectException e) {
            throw new GitException("Mit den internen Objekten ist etwas schief gelaufen \n" +
                "Fehlermeldung: " + e.getMessage());
        } catch (MissingObjectException e) {
            throw new GitException("Mit den internen Objekten ist etwas schief gelaufen \n" +
                "Fehlermeldung: " + e.getMessage());
        } catch (GitAPIException e) {
            throw new GitException("Mit Git ist etwasnicht genauer spezifiziertes schief gelaufen \n" +
                "Fehlermeldung: " + e.getMessage());
        }
    }


    /**
     * Get all stashes of the current Repository.
     *
     * @return A list of all Stashes
     */
    public List<GitStash> getStashes() {
        try {
            StashListCommand stashListCommand = new StashListCommand(repository);
            Collection<RevCommit> listOfStashes = stashListCommand.call();
            List<GitStash> gitStashes = new ArrayList<>();
            for (RevCommit stash : listOfStashes) {
                gitStashes.add(new GitStash(stash));
            }
            return gitStashes;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get the current status of the working directory.
     * Holds information about all active files and their current stages
     *
     * @return The singleton status object
     */
    public GitStatus getStatus() {
        return GitStatus.getGitStatus();
    }

    /**
     * Get a list of all configured remotes.
     *
     * @return A list of all remotes
     */
    public List<GitRemote> getRemotes() {
        try {
            List<RemoteConfig> remotes = git.remoteList().call();
            List<GitRemote> gitRemotes = new ArrayList<>();
            for (RemoteConfig config : remotes) {
                List<URIish> uris = config.getURIs();
                String user = uris.iterator().next().getUser();
                String name = config.getName();
                String urlString = uris.iterator().next().getPath();
                URIish uri = config.getURIs().iterator().next();
                URL url = new URL(uri.toString());
                gitRemotes.add(new GitRemote(url, user, name));
            }
            return gitRemotes;
        } catch (GitAPIException | MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to get a list of the branches, witch are available in the current repository.
     *
     * @return A list of branches in the repository
     */
    public List<GitBranch> getBranches() throws GitException {
        try {
            List<Ref> branches = git.branchList().call();
            List<GitBranch> gitBranches = new ArrayList<>();
            for (Ref branch : branches) {
                gitBranches.add(new GitBranch(branch));
            }
            return gitBranches;
        } catch (GitAPIException e) {
            throw new GitException("Mit Git ist etwas schief gelaufen" +
                "Fehlermeldung: " + e.getMessage());
        }
    }

    /**
     * Method to get list of branches, which are available in the specific online repository
     *
     * @param remote Online repository, where the branches come from
     * @return List of branches in the repository
     */
    public List<GitBranch> getBranches(GitRemote remote) {
//TODO: Überarbeiten
        List<Ref> branches; //creates a new list of branches from JGit
        List<GitBranch> toReturn = new ArrayList<>(); //List of Branches from Git Client
        try {
            branches = git.branchList().call();
            GitBranch branchToAdd;
            for (Ref branch : branches) {
                toReturn.add(new GitBranch(branch));

            }
        } catch (GitAPIException e) {

        }
        return toReturn;
    }

}

