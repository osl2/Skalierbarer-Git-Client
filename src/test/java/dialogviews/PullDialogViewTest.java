package dialogviews;

import commands.AbstractRemoteTest;
import git.GitBranch;
import git.GitCommit;
import git.GitRemote;
import git.exception.GitException;
import levels.Level;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import settings.Data;
import settings.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class PullDialogViewTest extends AbstractRemoteTest {
  PullDialogView pDV;
  JButton pullButton;
  JButton refreshButton;
  JComboBox remoteCombobox;
  JComboBox branchComboBox;

  @BeforeEach
  void setupComponents() throws IOException, GitAPIException {
    pDV = new PullDialogView();
    JPanel panel = pDV.getPanel();
    FindComponents find = new FindComponents();
    pullButton = (JButton) find.getChildByName(panel, "pullButton");
    assertNotNull(pullButton);
    refreshButton = (JButton) find.getChildByName(panel, "refreshButton");
    assertNotNull(refreshButton);
    remoteCombobox = (JComboBox) find.getChildByName(panel, "remoteCombobox");
    assertNotNull(remoteCombobox);
    branchComboBox = (JComboBox) find.getChildByName(panel, "branchComboBox");
    assertNotNull(branchComboBox);
  }

  @Test
  void testRefreshButton() {
    remoteCombobox.setSelectedIndex(0);
    branchComboBox.setSelectedIndex(0);
    refreshButton.getActionListeners()[0].actionPerformed(new ActionEvent(refreshButton, ActionEvent.ACTION_PERFORMED, null));
    assertNotEquals(0, branchComboBox.getSelectedIndex());
  }

  @Test
  void testPullButton() throws GitException, IOException, GitAPIException {
    Git jGit = Git.open(repo);
    FileWriter fr = new FileWriter(textFile, true);
    fr.write("pull");
    fr.close();
    jGit.add().addFilepattern(textFile.getName()).call();
    jGit.commit().setCommitter("Tester 5", "tester5@example.com").setSign(false)
            .setMessage("Commit 5").call();
    jGit.getRepository().close();
    jGit.close();
    repository.close();
    git.close();
   /* GitFacade f = new GitFacade();
    f.branchOperation(gitData.getSelectedBranch().getCommit(), "Neu");
    List<GitBranch> branches = gitData.getBranches();
    GitBranch b = null;
    for(int i = 0; i < branches.size(); i++) {
      if(branches.get(i).getName().compareTo("Neu") == 0) {
        b = branches.get(i);
      }
    }
    assertTrue(f.checkout(b));

    assertEquals("Commit 5", b.getCommit().getMessage());
    f.pushOperation(gitData.getRemotes().get(0), b);
    assertEquals(2, gitData.getBranches(gitData.getRemotes().get(0)).size());*/
    remoteCombobox.setSelectedIndex(0);
    branchComboBox.setSelectedIndex(0);
    pullButton.getActionListeners()[0].actionPerformed(new ActionEvent(pullButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.errorHandlerECalled);
    List<GitRemote> remoteList = gitData.getRemotes();
    assertEquals(1, remoteList.size());
    GitBranch remoteBranch = gitData.getBranches(remoteList.get(0)).get(0);
    GitBranch localBranch = gitData.getBranches().get(0);
    List<Level> levels = Data.getInstance().getLevels();
    Settings.getInstance().setLevel(levels.get(0));
    PullConflictDialogView conflict = new PullConflictDialogView(remoteBranch, localBranch, "pull");
    FindComponents find = new FindComponents();
    JButton mergeButton = (JButton) find.getChildByName(conflict.getPanel(), "mergeButton");
    assertNotNull(mergeButton);
    mergeButton.getActionListeners()[0].actionPerformed(new ActionEvent(mergeButton, ActionEvent.ACTION_PERFORMED, null));
    GitCommit commit = gitData.getCommits().next();
    assertEquals("Commit 5", commit.getMessage());
  }
}
