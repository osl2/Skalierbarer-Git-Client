package dialogviews;

import commands.Fetch;
import controller.GUIController;
import git.*;
import git.exception.GitException;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class FetchDialogView implements IDialogView {

  private JPanel FetchPanel;
  private JScrollPane treeScrollPanel;
  private JTree fetchTree;
  private JButton fetchButton;
  private JPanel bottomPanel;
  private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
  private DefaultTreeModel model;
  private boolean needNew = false;

  public FetchDialogView() {

    final GitData git ;
    git = new GitData();
    fetchTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    this.model = (DefaultTreeModel) this.fetchTree.getModel();
    // Build branch-tree

    for (GitRemote gitRemote : git.getRemotes()) {
      RemoteTreeNode remoteTreeNode = null;

      try {
        remoteTreeNode = buildRemoteTree(gitRemote);
      } catch (GitException e) {
        needNew = true;
        return;
      }
      root.add(remoteTreeNode);

    }

    this.fetchTree.setRootVisible(false);
    model.setRoot(root);
    // Todo: localize
    fetchButton.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        TreePath[] selected = fetchTree.getSelectionPaths();
        Fetch command = new Fetch();
        if (selected == null) {
          //todo: localize? GuiController?
          JOptionPane.showMessageDialog(null,
                  "Es muss ein Repo / ein Zweig ausgewählt werden",
                  "Fehler",
                  JOptionPane.ERROR_MESSAGE);

          return;
        }
        for (int i = 0; i < selected.length; i++){
          RefTreeNode node = (RefTreeNode) selected[i].getLastPathComponent();
          node.configureFetch(command);
        }
          if (command.execute()) {
            GUIController.getInstance().closeDialogView();
          }
          else {
            // todo let controller handle that
            JOptionPane.showMessageDialog(null,
                    "Es ist ein unerwarteter Fehler aufgetreten",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
          }

      }
    });
  }


  private RemoteTreeNode buildRemoteTree(GitRemote r) throws GitException {
    GitData git = null;
    RemoteTreeNode root = new RemoteTreeNode(r);

    git = new GitData();

    GitBranch[] branches = new GitBranch[0];
    branches = git.getBranches(r).toArray(new GitBranch[git.getBranches(r).size()]);
    for (int i = 0; i < branches.length; i++) {
      BranchTreeNode node = new BranchTreeNode(branches[i], r);
      root.add(node);
    }

    return root;
  }
  /**
   * DialogWindow Title
   *
   * @return Window Title as String
   */

  private static abstract class RefTreeNode extends DefaultMutableTreeNode {
    public RefTreeNode() {
    }

    abstract void configureFetch(Fetch fetch);
  }

  private static class RemoteTreeNode extends RefTreeNode {
    private final GitRemote remote;

    private RemoteTreeNode(GitRemote remote) {
      this.remote = remote;
    }

    public GitRemote getRemote() {
      return remote;
    }

    @Override
    public String toString() {
      return remote.getName();
    }

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

    @Override
    public String toString() {
      return branch.getName();
    }

    public GitBranch getBranch() {
      return branch;
    }

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
    Dimension dim = new Dimension(800, 600);
    return dim;
  }

  /**
   * The content Panel containing all contents of the Dialog
   *
   * @return the shown content
   */
  @Override
  public JPanel getPanel() {
    return FetchPanel;
  }

  public void update() {

  }

  public boolean isNeedNew() {
    return needNew;
  }
}
