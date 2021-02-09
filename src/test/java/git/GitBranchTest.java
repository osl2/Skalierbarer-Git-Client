package git;

import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GitBranchTest extends AbstractGitTest {

    @Test
    public void branchRefIsUpdated() throws GitAPIException, GitException {
        git.branchCreate().setName("Test1").call();
        git.branchCreate().setName("Test2").call();
        git.branchCreate().setName("Test3").call();

        List<GitBranch> branches = gitData.getBranches();
        GitBranch selected = branches.get(2); // arbitrary selection
        GitCommit oldHead = selected.getCommit();
        oldHead.getMessage();
        git.checkout().setName(selected.getFullName()).call();
        git.commit().setCommitter("Test2", "e@example.com").setSign(false).setMessage("msg").call();
        assertNotEquals(oldHead.getHash(), selected.getCommit().getHash());
    }

    @Test
    public void branchNameIsCorrectSpecialCharacters() throws GitAPIException, GitException {
        String[] expectedBranches = new String[]{"Test2", "Test3", "wip/test1"};
        for (String b : expectedBranches) {
            git.branchCreate().setName(b).call();
        }

        String[] branchNames = gitData.getBranches().stream()
                .map(GitBranch::getName)
                .filter(b -> !b.equals("master")) // dont include master
                .toArray(String[]::new);

        Arrays.sort(expectedBranches);
        Arrays.sort(branchNames);

        assertArrayEquals(expectedBranches, branchNames);

    }
}
