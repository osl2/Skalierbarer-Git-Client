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
package dialogviews;

import commands.Checkout;
import controller.GUIController;
import git.GitBranch;
import git.GitCommit;
import git.GitData;
import git.exception.GitException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;

/**
 * Opens a TreeView to select Commit or Branch to checkout.
 */
public class CheckoutDialogView implements IDialogView {

    private static final int MAX_BRANCH_DEPTH = 25;
    private static final int LOAD_MORE_DEPTH = 50;
    private JPanel contentPane;
    private JTree tree1;
    private JButton abortButton;
    private GitData git;
    private JButton okButton;
    @SuppressWarnings("unused")
    private JPanel bottomPanel;
    private DefaultTreeModel model;

    /**
     * Create a new view
     */
    public CheckoutDialogView() {
        git = new GitData();
        this.abortButton.setText("Abbrechen");
        this.okButton.setText("Ok");

        this.abortButton.addActionListener(e -> GUIController.getInstance().closeDialogView());
        this.okButton.addActionListener(this::okButtonHandler);

        this.model = (DefaultTreeModel) this.tree1.getModel();
        this.tree1.setRootVisible(false);

        model.setRoot(this.generateTree());
        nameComponents();
    }

    /**
     * This method is needed in order to execute the GUI tests successfully.
     * Do not change otherwise tests might fail.
     */
    private void nameComponents() {
        tree1.setName("tree1");
        okButton.setName("okButton");
        abortButton.setName("abortButton");
    }

    private DefaultMutableTreeNode generateTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        try {
            git.getBranches().stream().map(this::buildBranchTree).forEach(root::add);
        } catch (GitException | NullPointerException e) {
            GUIController.getInstance().errorHandler(e);
            // this is fatal for our view
            GUIController.getInstance().closeDialogView();
        }
        return root;
    }

    private BranchTreeNode buildBranchTree(GitBranch b) {
        BranchTreeNode root = new BranchTreeNode(b);
        Iterator<GitCommit> iter;
        try {
            iter = b.getCommits();
        } catch (GitException | IOException e) {
            GUIController.getInstance().errorHandler(e);
            // this is fatal for our view
            GUIController.getInstance().closeDialogView();
            return root;
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

    private void okButtonHandler(ActionEvent e) {
        TreePath selected = tree1.getSelectionPath();
        if (selected == null) {
            GUIController.getInstance().errorHandler("Es muss ein Zweig / eine Einbuchung ausgewählt werden");
            return;
        }
        RefTreeNode node = (RefTreeNode) tree1.getSelectionPath().getLastPathComponent();
        Checkout command = new Checkout();
        node.configureCheckout(command);

        if (command.execute()) {
            GUIController.getInstance().setCommandLine(command.getCommandLine());
            GUIController.getInstance().closeDialogView();
        } else {
            GUIController.getInstance().errorHandler("Es ist ein unerwarteter Fehler aufgetreten");
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
     * This method updates the Dialog, in this case it will reload the Branches and commits.
     */
    @Override
    public void update() {
        this.model.setRoot(generateTree());
    }

    private TreeSelectionListener nodeChangedListener() {
        return treeSelectionEvent -> {
            RefTreeNode node = (RefTreeNode) treeSelectionEvent.getPath().getLastPathComponent();
            node.onSelectListener(treeSelectionEvent);
        };

    }

    @SuppressWarnings("unused")
    private void createUIComponents() {
        this.tree1 = new JTree();
        this.tree1.addTreeSelectionListener(nodeChangedListener());
    }

    private abstract static class RefTreeNode extends DefaultMutableTreeNode {
        public RefTreeNode() {
        }

        protected void onSelectListener(TreeSelectionEvent e) {
            // NO OP
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

    private static class LoadMoreNode extends RefTreeNode {

        Iterator<GitCommit> iterator;
        DefaultTreeModel model;

        LoadMoreNode(Iterator<GitCommit> iterator, DefaultTreeModel model) {
            this.iterator = iterator;
            this.model = model;
        }

        @Override
        protected void onSelectListener(TreeSelectionEvent e) {
            super.onSelectListener(e);
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

        @Override
        void configureCheckout(Checkout checkout) {
            throw new AssertionError("This node should not be selectable");
        }
    }

}
