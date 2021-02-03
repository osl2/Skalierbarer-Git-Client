package dialogviews;

import commands.Checkout;
import controller.GUIController;
import git.GitBranch;
import git.GitCommit;
import git.GitData;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CheckoutDialogView implements IDialogView {

    private static final int MAX_BRANCH_DEPTH = 10;
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    private JPanel contentPane;
    private JTree tree1;
    private JButton abortButton;
    private final GitData git;
    private JButton okButton;

    public CheckoutDialogView() {
        git = new GitData();
        // Todo: localize
        this.abortButton.setText("Abbrechen");
        this.okButton.setText("Ok");

        this.abortButton.addActionListener(e -> GUIController.getInstance().closeDialogView(this));
        this.okButton.addActionListener(this::OkButtonHandler);

        // Build branch-tree
        git.getBranches().stream().map(this::buildBranchTree).forEach(root::add);

        this.tree1.setRootVisible(false);
        ((DefaultTreeModel) this.tree1.getModel()).setRoot(root);
    }

    private BranchTreeNode buildBranchTree(GitBranch b) {
        BranchTreeNode root = new BranchTreeNode(b);
        GitCommit c = b.getCommit();
        for (int i = 0; i < MAX_BRANCH_DEPTH; i++) {
            if (c.getParents().length > 1)
                break;

            CommitTreeNode node = new CommitTreeNode(c);
            root.add(node);
            c = c.getParents()[0];
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

        if (command.execute()) {
            GUIController.getInstance().closeDialogView(this);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Es ist ein unerwarteter Fehler aufgetreten",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
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
        return new Dimension(800, 600);
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


    private void createUIComponents() {
        this.tree1 = new JTree(this.root);
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
            // todo: use api method
            return commit.getHash().substring(commit.getHash().length() - 7) + " - " + commit.getMessage();
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

}