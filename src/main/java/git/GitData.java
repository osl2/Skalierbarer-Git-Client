package git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import settings.Settings;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Provides a central point to obtain and create a number of git objects.
 */
public class GitData {

    org.eclipse.jgit.api.Git git;
    Repository repository;

    public GitData(){
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
            System.out.println(e);
            //TODO: Fehlerbehandlung machen
        }
    }

    /**
     * Get all commits of the current Repository.
     *
     * @return Array of all commits without stashes
     */
    public List<GitCommit> getCommits() {

        throw new AssertionError("not implemented yet");
    }


    /**
     * Get all stashes of the current Repository.
     *
     * @return A list of all Stashes
     */
    public List<GitStash> getStashes() {
        throw new AssertionError("not implemented yet");
    }


    /**
     * Get the current status of the working directory.
     * Holds information about all active files and their current stages
     *
     * @return The singleton status object
     */
    public GitStatus getStatus() {
        throw new AssertionError("not implemented yet");
    }

    /**
     * Get a list of all configured remotes.
     *
     * @return A list of all remotes
     */
    public List<GitRemote> getRemotes() {
        throw new AssertionError("not implemented yet");
    }

    /**
     * Method to get a list of the branches, witch are available in the current repository.
     *
     * @return A list of branches in the repository
     */
    public List<GitBranch> getBranches() {
        try {
            List<Ref> branches = git.branchList().call();
            List<GitBranch> gitBranches = new ArrayList<>();
            for (Ref branch : branches) {
                gitBranches.add(getBranch(branch));
            }
            return gitBranches;
        } catch (GitAPIException e) {
            //TODO: Fehlerbehandlung
        } catch (MissingObjectException | IncorrectObjectTypeException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Method to get list of branches, which are available in the specific online repository
     *
     * @param remote Online repository, where the branches come from
     * @return List of branches in the repository
     */
    public List<GitBranch> getBranches(GitRemote remote) {

        List<Ref> branches; //creates a new list of branches from JGit
        List<GitBranch> toReturn = new ArrayList<>(); //List of Branches from Git Client
        try {
            branches = git.branchList().call();
            GitBranch branchToAdd;
            for (Ref branch : branches) {
                toReturn.add(getBranch(branch));

            }
        } catch (GitAPIException e) {
            //TODO: Exception fangen
        } catch (MissingObjectException | IncorrectObjectTypeException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    /**
     * Method to get the given Branch as a GitBranch
     * @param branchRef
     * @return
     * @throws GitAPIException
     * @throws IncorrectObjectTypeException
     * @throws MissingObjectException
     */
    private GitBranch getBranch (Ref branchRef) throws GitAPIException, IncorrectObjectTypeException, MissingObjectException {
        GitBranch gitBranch = new GitBranch();
        gitBranch.setName(branchRef.getName());
        //Gets a List of the newest Commits from the Branch
        Iterable<RevCommit> commits = git.log().add(branchRef.getObjectId()).setMaxCount(1).call();
        //Gets the first commit of the list above
        RevCommit newestCommit = commits.iterator().next();
        gitBranch.setHead(getCommit(newestCommit));
        return gitBranch;
    }

    /**
     * Method to create a GitCommit from a JGit reference
     *
     * @param revCommit RevCommit to the Commit that should be created
     * @return GitCommit, that is equivalent to the RevCommit
     */
    private GitCommit getCommit(RevCommit revCommit) {
        RevWalk walk = new RevWalk(repository);
        try {
            walk.parseHeaders(revCommit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String authorName = revCommit.getAuthorIdent().getName();
        String authorEMail = revCommit.getAuthorIdent().getEmailAddress();
        GitAuthor author = new GitAuthor(authorName, authorEMail);

        String message = revCommit.getFullMessage();

        List<GitCommit> parentsList = new ArrayList<>();
        RevCommit[] parentsRev = revCommit.getParents();
        for (RevCommit parent : parentsRev) { //Resolve for each parent
            assert parent != null;
            parentsList.add(getCommit(parent)); //Hier habe ich einen Nullpointer, wenn ich den Author des Parents o.ä. abfrage...
        }
        GitCommit[] parents = parentsList.toArray(GitCommit[]::new);

        String hash = revCommit.getName();

        boolean isSigned;
        isSigned = revCommit.getRawGpgSignature() != null;


        int commitTime = revCommit.getCommitTime();
        Instant instant = Instant.ofEpochSecond(commitTime);
        Date date = Date.from(instant);
        return new GitCommit (author, message, hash, isSigned, date, parents);
    }

}

