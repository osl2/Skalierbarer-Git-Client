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
@SuppressWarnings("unused")
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

    /**
     * Method to build the tree
     * @param b The branch for the TreeNode
     * @return The TreeNode of the Branch
     */
    private BranchTreeNode buildBranchTree(GitBranch b) {
        BranchTreeNode root = new BranchTreeNode(b);
        Iterator<GitCommit> iter = null;
        try {
            iter = b.getCommits();
        } catch (GitException | IOException e) {
            GUIController.getInstance().errorHandler(e);
        }
        int i = 0;
        if (iter != null) {
            while (i++ < MAX_BRANCH_DEPTH && iter.hasNext()) {
                CommitTreeNode node = new CommitTreeNode(iter.next());
                root.add(node);
            }
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
    @Override
    public void update() {

    }

    /**
     * Method to get the selectionListener for the tree, if there should be loaded more commits
     * @return the listener for the tree
     */
    private TreeSelectionListener loadMoreListener() {
        return treeSelectionEvent -> {
            if (treeSelectionEvent.getPath().getLastPathComponent() instanceof LoadMoreNode) {
                LoadMoreNode node = (LoadMoreNode) treeSelectionEvent.getPath().getLastPathComponent();
                node.loadMoreItems();

            }
        };

    }
    @SuppressWarnings("BoundFieldAssignment")
    private void createUIComponents() {
        this.revertTree = new JTree(this.root);
        this.revertTree.addTreeSelectionListener(loadMoreListener());
    }

    /**
     * Constructor to create an Instance of the class RevertDialogView
     */
    public RevertDialogView() {
        GitData testGit = new GitData();
        // If there are no commits, the user cant revert.
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
        // Add the loadMoreListener to the tree
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
                //Configures the revert-command
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
                } else if (rev.execute()) {
                    GUIController.getInstance().setCommandLine(rev.getCommandLine());
                    GUIController.getInstance().closeDialogView();
                }
            }
        });
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

        /**
         * Gets the commit of this treeNode
         * @return returns the commit
         */
        public GitCommit getCommit() {
            return commit;
        }

        /**
         * Method to get the String-representation of this class
         * @return the String-representation
         */
        @Override
        public String toString() {
            return commit.getHashAbbrev() + " - " + commit.getShortMessage();
        }

        /**
         * Method to configure an revert command
         * @param revert the command, which should be configured
         */
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
        /**
         * Method to get the String-representation of this class
         * @return the String-representation
         */
        @Override
        public String toString() {
            return branch.getName();
        }

        /**
         * Gets the branch of this node
         * @return returns the branch
         */
        public GitBranch getBranch() {
            return branch;
        }
        /**
         * Method to configure an revert command
         * @param revert the command, which should be configured
         */
        @Override
        void configureRevert(Revert revert) {

        }
    }

    private static class LoadMoreNode extends DefaultMutableTreeNode {

        Iterator<GitCommit> iterator;
        DefaultTreeModel model;

        /**
         * Constructor to create a new instance
         * @param iterator the iterator for the loadMore-Method
         * @param model the model, in which the items shuold be loaded
         */
        LoadMoreNode(Iterator<GitCommit> iterator, DefaultTreeModel model) {
            this.iterator = iterator;
            this.model = model;
        }

        /**
         * Method to load more Items with the iterator
         */
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
        /**
         * Method to get the String-representation of this class
         * @return the String-representation
         */
        @Override
        public String toString() {
            return "Load " + LOAD_MORE_DEPTH + " more";
        }
    }

}
