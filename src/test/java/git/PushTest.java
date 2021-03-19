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

import commands.AbstractRemoteTest;
import commands.Push;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PushTest extends AbstractRemoteTest {
    private Push push;
    private final String NEW_REMOTE_NAME = "testRemote";
    private final String NEW_BRANCH_NAME = "testBranch";
    private GitRemote newRemote;
    private GitBranch newLocalBranch;
    private GitRemote origin;

    @BeforeEach
    void setupPush() throws URISyntaxException, GitAPIException {
        //this comes from AbstractRemoteTest
        origin = new GitRemote(REMOTE_URI, "origin");

        push = new Push();
        newRemote = new GitRemote(REMOTE_URI, NEW_REMOTE_NAME);
        URIish remote_uri = new URIish(REMOTE_URI);
        //update git
        git = GitData.getJGit();
        repository = GitData.getRepository();
        git.remoteAdd().setUri(remote_uri).setName(NEW_REMOTE_NAME).call();

        boolean branchExists = false;
        List<Ref> branches = git.branchList().call();
        for (Ref ref : branches) {
            if (new GitBranch(ref.getName()).getName().compareTo(NEW_BRANCH_NAME) == 0) {
                branchExists = true;
            }
        }

        if (!branchExists) {
            git.checkout().setCreateBranch(true).setName(NEW_BRANCH_NAME).call();
        }

        //reload list of branches
        branches = git.branchList().call();
        for (Ref branchRef : branches) {
            GitBranch branch = new GitBranch(branchRef.getName());
            if (branch.getName().compareTo(NEW_BRANCH_NAME) == 0) {
                newLocalBranch = branch;
            }
        }
        assertNotNull(newLocalBranch);
    }

    @Test
    void getNameTest() {
        assertEquals(0, push.getName().compareTo("Push"));
    }

    @Test
    void getCommandLineTest() {
        push.setRemote(newRemote);
        push.setLocalBranch(newLocalBranch);
        assertEquals(0, push.getCommandLine().compareTo("git push testRemote testBranch"));

        push.setRemoteBranchName("remoteBranch");
        assertEquals(0, push.getCommandLine().compareTo("git push testRemote testBranch:remoteBranch"));

    }

    @Test
    void getDescriptionTest() {
        String description = "Lädt die lokalen Einbuchungen aus dem aktuellen Branch in das Online-Verzeichnis hoch";
        assertEquals(0, push.getDescription().compareTo(description));
    }

    @Test
    void testExecuteNoLocalBranchSet() {
        push.setRemote(newRemote);
        assertFalse(push.execute());
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    @Test
    void testExecuteNoRemoteSet() {
        push.setLocalBranch(newLocalBranch);
        assertFalse(push.execute());
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    @Test
    void onButtonClickedTest() {
        push.onButtonClicked();
        assertTrue(guiControllerTestable.openDialogCalled);
    }

    @Test
    void testExecuteNoRemoteBranchSet() throws GitAPIException {
        push.setLocalBranch(newLocalBranch);
        push.setRemote(newRemote);
        assertTrue(push.execute());

        assertTrue(isBranchInRemote(newLocalBranch.getName(), NEW_REMOTE_NAME));

    }

    @Test
    void testExecuteRemoteBranchDifferentName() throws GitAPIException {
        String modifiedRemoteBranchName = NEW_BRANCH_NAME + "_modified";
        push.setLocalBranch(newLocalBranch);
        push.setRemote(newRemote);
        //modify name of destination branch
        push.setRemoteBranchName(modifiedRemoteBranchName);

        assertTrue(push.execute());

        assertTrue(isBranchInRemote(modifiedRemoteBranchName, NEW_REMOTE_NAME));
    }


    @Test
    void testExecutePushToExistingUpstreamBranch() throws GitAPIException {
        //origin should already have a master branch
        assertTrue(isBranchInRemote("master", origin.getName()));

        push.setRemote(origin);
        push.setLocalBranch(new GitBranch("master"));
        assertTrue(push.execute());
    }

    @Test
    void testExecuteOtherRemoteBranchShouldThrowException() throws GitAPIException {
        //push master to remote so that it already exists on remote
        git.push().setRefSpecs(new RefSpec("master")).call();
        git.fetch().setRemote(NEW_REMOTE_NAME).call();
        List<Ref> remoteBranches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        assertFalse(remoteBranches.isEmpty());

        //configure push command
        push.setLocalBranch(newLocalBranch);
        push.setRemote(newRemote);
        //try to push from non-master branch to already existing master branch
        push.setRemoteBranchName("master");

        assertFalse(push.execute());
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
    }

    private boolean isBranchInRemote(String branchName, String remoteName) throws GitAPIException {
        git.fetch().setRemote(remoteName).call();
        List<Ref> remoteBranches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        assertFalse(remoteBranches.isEmpty());
        Ref correct = null;
        for (Ref remoteBranch : remoteBranches) {
            GitBranch remoteGitBranch = new GitBranch(remoteBranch);
            if (remoteGitBranch.getName().compareTo(branchName) == 0) {
                correct = remoteBranch;
            }
        }
        return correct != null;
    }

}
