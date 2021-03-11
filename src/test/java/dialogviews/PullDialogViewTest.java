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
    assertEquals(0, branchComboBox.getSelectedIndex());
  }

  @Test
  void testPullButton() throws GitException, IOException, GitAPIException {
    // Create a new Commit to pull.
    git = Git.open(repo);
    FileWriter fr = new FileWriter(textFile, true);
    fr.write("pull");
    fr.close();
    git.add().addFilepattern(textFile.getName()).call();
    git.commit().setCommitter("Tester 5", "tester5@example.com").setSign(false)
            .setMessage("Commit 5").call();
    // Execute the Pull command in
    remoteCombobox.setSelectedIndex(0);
    branchComboBox.setSelectedIndex(0);
    List<Level> levels = Data.getInstance().getLevels();
    Settings.getInstance().setLevel(levels.get(0));
    pullButton.getActionListeners()[0].actionPerformed(new ActionEvent(pullButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.openDialogCalled);
    List<GitRemote> remoteList = gitData.getRemotes();
    assertEquals(1, remoteList.size());
    GitBranch remoteBranch = gitData.getBranches(remoteList.get(0)).get(0);
    GitBranch localBranch = gitData.getBranches().get(0);
    PullConflictDialogView conflict = new PullConflictDialogView(remoteBranch, localBranch, "pull");
    FindComponents find = new FindComponents();
    JButton mergeButton = (JButton) find.getChildByName(conflict.getPanel(), "mergeButton");
    assertNotNull(mergeButton);
    mergeButton.getActionListeners()[0].actionPerformed(new ActionEvent(mergeButton, ActionEvent.ACTION_PERFORMED, null));
    GitCommit commit = gitData.getCommits().next();
    assertEquals("Commit 5", commit.getMessage());
  }


  @Test
  void testMetaDataPullConflictView() {
    Settings.getInstance().setLevel(Data.getInstance().getLevels().get(3));
    PullConflictDialogView conflict = new PullConflictDialogView(null, null, "pull");
    assertNotNull(conflict.getDimension());
    assertNotNull(conflict.getTitle());
    conflict.update();
    FindComponents find = new FindComponents();
    JButton cancelButton = (JButton) find.getChildByName(conflict.getPanel(), "cancelButton");
    assertNotNull(cancelButton);
    cancelButton.getActionListeners()[0].actionPerformed(new ActionEvent(cancelButton, ActionEvent.ACTION_PERFORMED, null));
    assertTrue(guiControllerTestable.closeDialogViewCalled);
  }

  @Test
  void testMetaDataPullDialogView() {
    pDV.update();
    assertNotNull(pDV.getDimension());
    assertNotNull(pDV.getTitle());
  }
}
