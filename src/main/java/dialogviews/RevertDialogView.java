package dialogviews;

import commands.Revert;
import controller.GUIController;
import git.GitBranch;
import git.GitCommit;
import git.GitData;
import git.exception.GitException;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;

public class RevertDialogView implements IDialogView {


    private JPanel panel1;
    private JPanel RevertPanel;
    private JScrollPane treeScrollPanel;
    private JTree revertTree;
    private JButton revertButton;
    private JPanel bottomPanel;
    private static final int MAX_BRANCH_DEPTH = 25;
    private static final int LOAD_MORE_DEPTH = 50;
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    private DefaultTreeModel model;

    private BranchTreeNode buildBranchTree(GitBranch b) {
        BranchTreeNode root = new BranchTreeNode(b);
        Iterator<GitCommit> iter = null;
        try {
            iter = b.getCommits();
        } catch (GitException e) {
            GUIController.getInstance().errorHandler(e);
        } catch (IOException e) {
            GUIController.getInstance().errorHandler(e);
        }
        int i = 0;
        while (i++ < MAX_BRANCH_DEPTH && iter.hasNext()) {
            CommitTreeNode node = new CommitTreeNode(iter.next());
            root.add(node);
        }

        // We ran into our load maximum but the iterator had more nodes.
        if (i > MAX_BRANCH_DEPTH) {
            root.add(new LoadMoreNode(iter, model));
        }

        return root;
    }


    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Revert";
    }

    /**
     * The size of the Dialog
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
        return RevertPanel;
    }

    /**
     * method that is called to update the dialog view.
     */
    public void update() {

    }

    private TreeSelectionListener loadMoreListener() {
        return treeSelectionEvent -> {
            // todo: Come up with a better way of loading more nodes
            if (treeSelectionEvent.getPath().getLastPathComponent() instanceof LoadMoreNode) {
                LoadMoreNode node = (LoadMoreNode) treeSelectionEvent.getPath().getLastPathComponent();
                node.loadMoreItems();

            }
        };

    }

    private void createUIComponents() {
        this.revertTree = new JTree(this.root);
        this.revertTree.addTreeSelectionListener(loadMoreListener());
    }

    public RevertDialogView() {
        GitData testGit = new GitData();
        try {
            if (testGit.getBranches().size() == 0) {
                GUIController.getInstance().errorHandler("Es existiert kein Commit");
                GUIController.getInstance().closeDialogView();
                return;
            }
        } catch (GitException e) {
            GUIController.getInstance().errorHandler(e);
            GUIController.getInstance().closeDialogView();
        }
        this.revertTree.addTreeSelectionListener(loadMoreListener());
        try {
            final GitData git;
            git = new GitData();
            revertTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            this.model = (DefaultTreeModel) this.revertTree.getModel();

            // Build branch-tree
            git.getBranches().stream().map(this::buildBranchTree).forEach(root::add);

            this.revertTree.setRootVisible(false);
            model.setRoot(root);

        } catch (GitException e) {
            GUIController.getInstance().errorHandler(e);
        }
        revertButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Revert rev = new Revert();
                TreePath path = revertTree.getSelectionPath();
                if (path == null) {
                    GUIController.getInstance().errorHandler("Es wurde nichts ausgewählt");
                    return;
                }
                RefTreeNode node = (RefTreeNode) path.getLastPathComponent();
                node.configureRevert(rev);
                if (rev.getChosenCommit() == null) {
                    GUIController.getInstance().errorHandler("Es muss ein Commit ausgewählt sein.");
                    return;
                } else if (rev.execute()) {
                    GUIController.getInstance().setCommandLine(rev.getCommandLine());
                    GUIController.getInstance().closeDialogView();
                }
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        RevertPanel = new JPanel();
        RevertPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(RevertPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(800, 600), null, 0, false));
        treeScrollPanel = new JScrollPane();
        RevertPanel.add(treeScrollPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        revertTree = new JTree();
        revertTree.setRootVisible(false);
        treeScrollPanel.setViewportView(revertTree);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 7, new Insets(0, 0, 0, 0), -1, -1));
        RevertPanel.add(bottomPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        bottomPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        bottomPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(20, -1), new Dimension(20, -1), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        bottomPanel.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        bottomPanel.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        bottomPanel.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        bottomPanel.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        revertButton = new JButton();
        revertButton.setText("Revert");
        bottomPanel.add(revertButton, new com.intellij.uiDesigner.core.GridConstraints(1, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer7 = new com.intellij.uiDesigner.core.Spacer();
        bottomPanel.add(spacer7, new com.intellij.uiDesigner.core.GridConstraints(2, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 20), new Dimension(-1, 20), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer8 = new com.intellij.uiDesigner.core.Spacer();
        bottomPanel.add(spacer8, new com.intellij.uiDesigner.core.GridConstraints(0, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 20), new Dimension(-1, 20), new Dimension(-1, 20), 0, false));
    }

    private static abstract class RefTreeNode extends DefaultMutableTreeNode {
        public RefTreeNode() {
        }

        abstract void configureRevert(Revert revert);
    }

    private static class CommitTreeNode extends RefTreeNode {
        private final GitCommit commit;

        private CommitTreeNode(GitCommit commit) {
            this.commit = commit;
        }

        public GitCommit getCommit() {
            return commit;
        }

        @Override
        public String toString() {
            return commit.getHashAbbrev() + " - " + commit.getShortMessage();
        }

        @Override
        void configureRevert(Revert revert) {
            revert.setChosenCommit(commit);
        }
    }

    private static class BranchTreeNode extends RefTreeNode {
        private final GitBranch branch;

        private BranchTreeNode(GitBranch branch) {
            this.branch = branch;
        }

        @Override
        public String toString() {
            return branch.getName();
        }

        public GitBranch getBranch() {
            return branch;
        }

        @Override
        void configureRevert(Revert revert) {

        }
    }

    private static class LoadMoreNode extends DefaultMutableTreeNode {

        Iterator<GitCommit> iterator;
        DefaultTreeModel model;

        LoadMoreNode(Iterator<GitCommit> iterator, DefaultTreeModel model) {
            this.iterator = iterator;
            this.model = model;
        }

        void loadMoreItems() {
            int i = 0;
            while (i++ < LOAD_MORE_DEPTH && iterator.hasNext()) {
                parent.insert(new CommitTreeNode(iterator.next()), parent.getChildCount());
            }
            RefTreeNode oldParent = (RefTreeNode) this.parent;
            this.removeFromParent();
            if (i > LOAD_MORE_DEPTH && iterator.hasNext()) {
                oldParent.insert(this, oldParent.getChildCount());
            }

            model.reload(oldParent);

        }

        @Override
        public String toString() {
            return "Load " + LOAD_MORE_DEPTH + " more";
        }
    }

}
