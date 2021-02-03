package dialogviews;

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

    private JPanel contentPane;
    private JTree tree1;
    private JButton abortButton;
    private JButton okButton;
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();

    public CheckoutDialogView() {

        // Todo: localize
        this.abortButton.setText("Abbrechen");
        this.okButton.setText("Ok");

        this.abortButton.addActionListener(e -> GUIController.getInstance().closeDialogView(this));
        this.okButton.addActionListener(this::OkButtonHandler);

        new GitData().getBranches().forEach(b -> this.root.add(new BranchTreeNode(b)));

        DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
        vegetableNode.add(new DefaultMutableTreeNode("Capsicum"));
        vegetableNode.add(new DefaultMutableTreeNode("Carrot"));
        vegetableNode.add(new DefaultMutableTreeNode("Tomato"));
        vegetableNode.add(new DefaultMutableTreeNode("Potato"));

        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits");
        fruitNode.add(new DefaultMutableTreeNode("Banana"));
        fruitNode.add(new DefaultMutableTreeNode("Mango"));
        fruitNode.add(new DefaultMutableTreeNode("Apple"));
        fruitNode.add(new DefaultMutableTreeNode("Grapes"));
        fruitNode.add(new DefaultMutableTreeNode("Orange"));
        //add the child nodes to the root node
        this.root.add(vegetableNode);
        this.root.add(fruitNode);
        this.tree1.setRootVisible(false);


        ((DefaultTreeModel) this.tree1.getModel()).setRoot(root);
    }


    private void OkButtonHandler(ActionEvent e) {
        TreePath path = tree1.getSelectionPath();
    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Titel";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(400, 400);
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
     * method that ist called to update the dialog view.
     */
    public void update() {

    }

    private void createUIComponents() {
        this.tree1 = new JTree(this.root);
    }

    private class CommitTreeNode extends DefaultMutableTreeNode {
        private final GitCommit commit;

        private CommitTreeNode(GitCommit commit) {
            this.commit = commit;
        }

        public GitCommit getCommit() {
            return commit;
        }

        @Override
        public String toString() {
            return commit.getHash();
        }
    }

    private class BranchTreeNode extends DefaultMutableTreeNode {
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
    }

}