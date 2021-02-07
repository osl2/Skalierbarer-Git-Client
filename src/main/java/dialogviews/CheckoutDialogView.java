package dialogviews;

import commands.Checkout;
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;

public class CheckoutDialogView implements IDialogView {

    private static final int MAX_BRANCH_DEPTH = 25;
    private static final int LOAD_MORE_DEPTH = 50;
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    private JPanel contentPane;
    private JTree tree1;
    private JButton abortButton;
    private final GitData git;
    private JButton okButton;
    private JPanel bottomPanel;
    private DefaultTreeModel model;

    public CheckoutDialogView() {
        git = new GitData();
        // Todo: localize
        this.abortButton.setText("Abbrechen");
        this.okButton.setText("Ok");

        this.abortButton.addActionListener(e -> GUIController.getInstance().closeDialogView());
        this.okButton.addActionListener(this::OkButtonHandler);

        this.model = (DefaultTreeModel) this.tree1.getModel();

        // Build branch-tree
        git.getBranches().stream().map(this::buildBranchTree).forEach(root::add);

        this.tree1.setRootVisible(false);
        model.setRoot(root);
    }

    private BranchTreeNode buildBranchTree(GitBranch b) {
        BranchTreeNode root = new BranchTreeNode(b);
        Iterator<GitCommit> iter = b.getCommits();
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


    private void OkButtonHandler(ActionEvent e) {
        TreePath selected = tree1.getSelectionPath();
        if (selected == null) {
            //todo: localize? GuiController?
            JOptionPane.showMessageDialog(null,
                    "Es muss ein Zweig / eine Einbuchung ausgewählt werden",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }
        RefTreeNode node = (RefTreeNode) tree1.getSelectionPath().getLastPathComponent();
        Checkout command = new Checkout();
        node.configureCheckout(command);

        try {
            if (command.execute()) {
                GUIController.getInstance().closeDialogView();
            } else {
                // todo let controller handle that
                JOptionPane.showMessageDialog(null,
                        "Es ist ein unerwarteter Fehler aufgetreten",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (GitException gitException) {
            // todo let controller handle that
            gitException.printStackTrace();
        }
    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Checkout";
    }

    /**
     * The size of the Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return this.contentPane.getPreferredSize();
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return this.contentPane;
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
        this.tree1 = new JTree(this.root);
        this.tree1.addTreeSelectionListener(loadMoreListener());
    }

    private static abstract class RefTreeNode extends DefaultMutableTreeNode {
        public RefTreeNode() {
        }

        abstract void configureCheckout(Checkout checkout);
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
        void configureCheckout(Checkout checkout) {
            checkout.setDestination(commit);
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
        void configureCheckout(Checkout checkout) {
            checkout.setDestination(branch);
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