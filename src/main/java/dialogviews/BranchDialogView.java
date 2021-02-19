package dialogviews;

import commands.Branch;
import controller.GUIController;
import git.GitBranch;
import git.GitCommit;
import git.GitData;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BranchDialogView implements IDialogView {
    private List<GitBranch> branches;
    private LinkedList<GitCommit> commits = new LinkedList<GitCommit>();
    private JPanel panel1;
    private JPanel BranchPanel;
    private JComboBox branchComboBox;
    private JComboBox commitComboBox;
    private JTextField nameField;
    private JButton branchButton;
    private JLabel branchLabel;
    private JLabel commitLabel;
    private JLabel nameLabel;
    private GitData gitData = new GitData();

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }


    public BranchDialogView() {
        commitComboBox.setRenderer(new BranchDialogRenderer());
        commitComboBox.setMaximumRowCount(6);
        try {
            branches = gitData.getBranches();
        } catch (GitException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < branches.size(); i++) {
            branchComboBox.addItem(branches.get(i).getName());
        }


        branchComboBox.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = branchComboBox.getSelectedIndex();
                Iterator<GitCommit> it = null;
                GitBranch actualBranch = branches.get(index);
                commits.clear();
                commitComboBox.removeAllItems();
                try {
                    it = actualBranch.getCommits();
                } catch (GitException gitException) {
                    GUIController.getInstance().errorHandler(gitException);
                } catch (IOException ioException) {
                    GUIController.getInstance().errorHandler(ioException);
                }
                int count = 0;
                while (it.hasNext()) {
                    commits.add(count, it.next());
                    count++;
                }
                for (int i = 0; i < commits.size(); i++) {
                    commitComboBox.addItem(commits.get(i).getHashAbbrev() + " - " + commits.get(i).getMessage());
                }
            }
        });
        branchButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (commitComboBox.getSelectedItem() == null) {
                    GUIController.getInstance().errorHandler("Es muss ein Commit ausgewählt werden. Beim Auswählen eines" +
                            "Branches wird standartmäßig der letzte Commit ausgewählt");
                    return;
                }
                GitCommit commitForOp = commits.get(commitComboBox.getSelectedIndex());
                String nameOfBranch = nameField.getText();
                if (nameOfBranch.compareTo("") == 0) {
                    GUIController.getInstance().errorHandler("Es muss ein Name eingegeben werden");
                    return;
                }
                Branch branch = new Branch();
                branch.setBranchPoint(commitForOp);
                branch.setBranchName(nameOfBranch);
                if (branch.execute()) {
                    GUIController.getInstance().setCommandLine(branch.getCommandLine());
                    GUIController.getInstance().closeDialogView();
                }
            }
        });
    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Branch";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(800, 300);
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return BranchPanel;
    }

    public void update() {

    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        BranchPanel = new JPanel();
        BranchPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 8, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(BranchPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        branchComboBox = new JComboBox();
        BranchPanel.add(branchComboBox, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 0, false));
        commitComboBox = new JComboBox();
        BranchPanel.add(commitComboBox, new com.intellij.uiDesigner.core.GridConstraints(0, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, 25), new Dimension(300, 25), new Dimension(300, 25), 0, false));
        branchLabel = new JLabel();
        branchLabel.setText("Branch:");
        BranchPanel.add(branchLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(46, 16), null, 0, false));
        commitLabel = new JLabel();
        commitLabel.setText("Commit:");
        BranchPanel.add(commitLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        BranchPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(25, -1), new Dimension(25, -1), new Dimension(25, -1), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        BranchPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 7, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(25, -1), new Dimension(25, -1), new Dimension(25, -1), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        BranchPanel.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(1, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(50, -1), new Dimension(50, -1), new Dimension(50, -1), 0, false));
        branchButton = new JButton();
        branchButton.setText("Branch");
        BranchPanel.add(branchButton, new com.intellij.uiDesigner.core.GridConstraints(2, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameField = new JTextField();
        BranchPanel.add(nameField, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 0, false));
        nameLabel = new JLabel();
        nameLabel.setText("Name:");
        BranchPanel.add(nameLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(46, 16), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private static class BranchDialogRenderer extends JTextArea implements ListCellRenderer {
        private int minRows;

        public BranchDialogRenderer() {
            this.setLineWrap(true);
            this.setWrapStyleWord(true);
        }

        @Override
        public Component getListCellRendererComponent(final JList list,
                                                      final Object value, final int index, final boolean isSelected,
                                                      final boolean hasFocus) {
            Color background = Color.WHITE;
            this.setText((String) value);
            // Only the first 6 lines of the commit message should be shown;
            //this.setRows(minRows);
            int width = list.getWidth();
            if (isSelected) {
                // This color is light blue.
                background = new Color(0xAAD8E6);
            }
            this.setBackground(background);
            // this is just to activate the JTextAreas internal sizing mechanism
            //if (width > 0) {
            this.setSize(width, Short.MAX_VALUE);
            //}
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            return this;

        }
    }
}