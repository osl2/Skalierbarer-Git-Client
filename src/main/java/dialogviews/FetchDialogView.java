package dialogviews;

import commands.Fetch;
import controller.GUIController;
import git.CredentialProviderHolder;
import git.GitBranch;
import git.GitData;
import git.GitRemote;
import git.exception.GitException;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

/**
 * This class creates a DialogView to execute the {@link Fetch} command.
 * You can choose one or more Remotes or one or more Remote Branch to fetch.
 */
public class FetchDialogView implements IDialogView {

  private JPanel fetchPanel;
  @SuppressWarnings("unused")
  private JScrollPane treeScrollPanel;
  private JTree fetchTree;
  private JButton fetchButton;
  @SuppressWarnings("unused")
  private JPanel bottomPanel;
  private boolean isOpen = true;

  /**
   * Constructor to create a new Instance
   */
  public FetchDialogView() {
    setNameComponents();
    final GitData git;
    git = new GitData();
    fetchTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    DefaultTreeModel model = (DefaultTreeModel) this.fetchTree.getModel();
    // Build branch-tree

    DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    for (GitRemote gitRemote : git.getRemotes()) {
      RemoteTreeNode remoteTreeNode;

      try {
        remoteTreeNode = buildRemoteTree(gitRemote);
      } catch (Exception e) {
        return;
      }
      root.add(remoteTreeNode);

    }

    this.fetchTree.setRootVisible(false);
    model.setRoot(root);
    fetchButton.addActionListener(e -> {
      // Gets the selcted Branches and remotes
      TreePath[] selected = fetchTree.getSelectionPaths();
      Fetch command = new Fetch();
      if (selected == null) {
        GUIController.getInstance().errorHandler("Es muss ein branch oder remote ausgewählt werden");
        return;
      }
      for (TreePath treePath : selected) {
        RefTreeNode node = (RefTreeNode) treePath.getLastPathComponent();
        node.configureFetch(command);
      }
      // If executes change commandline and close dialogview
      if (command.execute()) {
        GUIController.getInstance().setCommandLine(command.getCommandLine());
        GUIController.getInstance().closeDialogView();
      } else {
        GUIController.getInstance().errorHandler("Es ist ein unerwarteter Fehler Aufgetreten");
      }

    });
  }

  /**
   * This method is needed in order to execute the GUI tests successfully.
   * Do not change otherwise tests might fail.
   */
  private void setNameComponents() {
    fetchTree.setName("fetchTree");
    fetchButton.setName("fetchButton");
  }

  private RemoteTreeNode buildRemoteTree(GitRemote r) {
    RemoteTreeNode root = new RemoteTreeNode(r);
    GitBranch[] branches;
    branches = loadRemoteBranches(r);
    if (branches == null) {
      return null;
    }
    for (GitBranch branch : branches) {
      BranchTreeNode node = new BranchTreeNode(branch, r);
      root.add(node);
    }

    return root;
  }

  private abstract static  class RefTreeNode extends DefaultMutableTreeNode {
    public RefTreeNode() {
    }

    abstract void configureFetch(Fetch fetch);
  }

  private static class RemoteTreeNode extends RefTreeNode {
    private final GitRemote remote;

    private RemoteTreeNode(GitRemote remote) {
      this.remote = remote;
    }

    /**
     * Gets the remote this node holds
     * @return remote this node holds
     */
    public GitRemote getRemote() {
      return remote;
    }

    /**
     * Gets the string-representation of this class
     * @return string-representation
     */
    @Override
    public String toString() {
      return remote.getName();
    }

    /**
     * Configures a fetch command
     * @param fetch the fetch command, on which the operation is executed
     */
    @Override
    void configureFetch(Fetch fetch) {
      fetch.addRemote(remote);
    }
  }

  private static class BranchTreeNode extends RefTreeNode {
    private final GitRemote remote;
    private final GitBranch branch;

    private BranchTreeNode(GitBranch branch, GitRemote remote) {
      this.branch = branch;
      this.remote = remote;
    }

    /**
     * Gets the string-representation of this class
     * @return string-representation
     */
    @Override
    public String toString() {
      return branch.getName();
    }

    /**
     * Method to get the branch of this node
     * @return GitBranch
     */
    public GitBranch getBranch() {
      return branch;
    }

    /**
     * Configures a fetch command
     * @param fetch the fetch command, on which the operation is executed
     */
    @Override
    void configureFetch(Fetch fetch) {
      fetch.addBranch(remote, branch);
    }
  }

  /**
   * DialogWindow Title
   *
   * @return Window Title as String
   */
  @Override
  public String getTitle() {
    return "Fetch";
  }

  /**
   * The Size of the newly created Dialog
   *
   * @return 2D Dimension
   */
  @Override
  public Dimension getDimension() {
    return new Dimension(800, 600);
  }

  /**
   * The content Panel containing all contents of the Dialog
   *
   * @return the shown content
   */
  @Override
  public JPanel getPanel() {
    return fetchPanel;
  }

  @Override
  public void update() {
    //Is not needed for now
  }

  /**
   * Returns if this window can be opened
   * @return true if its possible false else
   */
  public boolean isOpen() {
    return isOpen;
  }

  private GitBranch[] loadRemoteBranches(GitRemote r) {

    return reloadBranches(r);

  }

  private GitBranch[] reloadBranches(GitRemote r) {
    GitData git = new GitData();
    GitBranch[] ret = null;
    try {
      ret = git.getBranches(r).toArray(new GitBranch[0]);
    } catch (GitException e) {
      CredentialProviderHolder.getInstance().changeProvider(true, r.getName());
      if (CredentialProviderHolder.getInstance().isActive()) {
        return loadRemoteBranches(r);
      } else {
        isOpen = false;
      }

    }
    return ret;
  }
}
