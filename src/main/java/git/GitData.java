package git;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import org.eclipse.jgit.revwalk.RevCommit;
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
            builder.setGitDir(path);
            repository = builder.build();
            git = git.open(path);
        } catch (IOException e) {
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
//        List<Ref> branches = git.branchList().call();
        throw new AssertionError("not implemented yet");
    }

    /**
     * Method to get list of branches, which are available in the specific online repository
     *
     * @param remote Online repository, where the branches come from
     * @return List of branches in the repository
     */
    public List<GitBranch> getBranches(GitRemote remote) {

        List<Ref> branches = null; //creates a new list of branches from JGit
        List<GitBranch> toReturn = new ArrayList<>(); //List of Branches from Git Client
        try {
            branches = git.branchList().call();
        GitBranch branchToAdd;
        for (Ref branch : branches){
            toReturn.add(getBranch(branch));

//           branchToAdd = new GitBranch();
//           branchToAdd.setName(branch.getName());
//           RevCommit commit = git.log().setMaxCount(1).call().iterator().next();
//           branchToAdd.setHead(getCommit(commit));
//           toReturn.add(branchToAdd);
        }
        } catch (GitAPIException e) {
            //TODO: Exception fangen
        }
        return toReturn;
    }

    private GitBranch getBranch (Ref branchRef) throws GitAPIException {
        GitBranch gitBranch = new GitBranch();
        gitBranch.setName(branchRef.getName());

        //Gets the last commit in the branch
        System.out.println(branchRef.getName());
        RevCommit checkedOut = git.log().setMaxCount(1).call().iterator().next();
        git.checkout().setName(branchRef.getName()).call();
        RevCommit newestCommitOfBranch = git.log().setMaxCount(1).call().iterator().next();
        git.checkout().setName(checkedOut.getName()).call();

        gitBranch.setHead(getCommit(newestCommitOfBranch));
        return gitBranch;
    }

    private GitCommit getCommit (RevCommit revCommit){
        String authorName = revCommit.getAuthorIdent().getName();
        String authorEMail = revCommit.getAuthorIdent().getEmailAddress();
        GitAuthor author = new GitAuthor(authorName, authorEMail);

        String message = revCommit.getFullMessage();

        List <GitCommit> parentsList = new ArrayList<>();
        RevCommit[] parentsRev = revCommit.getParents();
        for (RevCommit parent : parentsRev){ //Resolve for each parent
            parentsList.add(getCommit(parent));
        }
        GitCommit[] parents = parentsList.toArray(GitCommit[]::new);

        String hash = revCommit.getName();

        boolean isSigned;
        if (revCommit.getRawGpgSignature() != null){
            isSigned = true;
        } else {
            isSigned = false;
        }


        int commitTime = revCommit.getCommitTime();
        Instant instant = Instant.ofEpochSecond(commitTime);
        Date date = Date.from(instant);
        return new GitCommit (author, message, hash, isSigned, date, parents);
    }

}

