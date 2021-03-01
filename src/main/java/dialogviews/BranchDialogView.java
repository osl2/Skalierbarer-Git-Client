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

/**
 * Class of the DialogView from the command branch
 */
public class BranchDialogView implements IDialogView {
    private List<GitBranch> branches;
    private final LinkedList<GitCommit> commits = new LinkedList<>();
    private JPanel panel1;
    private JPanel BranchPanel;
    private JComboBox<String> branchComboBox;
    private JComboBox<String> commitComboBox;
    private JTextField nameField;
    private JButton branchButton;
    private JLabel branchLabel;
    private JLabel commitLabel;
    private JLabel nameLabel;

    /**
     * Constructor for a new Instance
     */
    public BranchDialogView() {
        commitComboBox.setRenderer(new BranchDialogRenderer());
        commitComboBox.setMaximumRowCount(6);
        // Adds the existing branches to the comboBox
        try {
            GitData gitData = new GitData();
            branches = gitData.getBranches();
        } catch (GitException e) {
            e.printStackTrace();
        }
        for (GitBranch branch : branches) {
            branchComboBox.addItem(branch.getName());
        }
        // Load the commits of the actual Branch in the commit-Combobox
        reloadCommits();

        branchComboBox.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
               reloadCommits();
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
                // checks if a commit is selected
                if (commitComboBox.getSelectedItem() == null) {
                    GUIController.getInstance().errorHandler("Es muss ein Commit ausgewählt werden. Beim Auswählen eines" +
                            "Branches wird standartmäßig der letzte Commit ausgewählt");
                    return;
                }
                // Gets the commit from the combobox an the Name of the new Branch frpm the textfield
                GitCommit commitForOp = commits.get(commitComboBox.getSelectedIndex());
                String nameOfBranch = nameField.getText();
                // Checks if there is a name
                if (nameOfBranch.compareTo("") == 0) {
                    GUIController.getInstance().errorHandler("Es muss ein Name eingegeben werden");
                    return;
                }
                Branch branch = new Branch();
                branch.setBranchPoint(commitForOp);
                branch.setBranchName(nameOfBranch);
                // Executes the command and closes the dialogview and sets the command line if the command was succesfull
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
     * ListCellrenderer for the commitCombobox
     */
    private static class BranchDialogRenderer extends JTextArea implements ListCellRenderer {
        /**
         * Constructor for a new instance
         */
        public BranchDialogRenderer() {
            this.setLineWrap(true);
            this.setWrapStyleWord(true);
        }

        /**
         * Method to get the rendered component
         * @param list The list of the normal Components
         * @param value the value of the component
         * @param index the index of the component
         * @param isSelected if the component is selected
         * @param hasFocus if the component has focus
         * @return the new Component
         */
        @Override
        public Component getListCellRendererComponent(final JList list,
                                                      final Object value, final int index, final boolean isSelected,
                                                      final boolean hasFocus) {
            Color background = Color.WHITE;
            this.setText((String) value);
            int width = list.getWidth();
            if (isSelected) {
                // This color is light blue.
                background = new Color(0xAAD8E6);
            }
            this.setBackground(background);
            // this is just to activate the JTextAreas internal sizing mechanism
            this.setSize(width, Short.MAX_VALUE);
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            return this;

        }
    }

    /**
     * Method to reload the commits
     */
    private void reloadCommits(){
        int index = branchComboBox.getSelectedIndex();
        Iterator<GitCommit> it = null;
        GitBranch actualBranch = branches.get(index);
        commits.clear();
        commitComboBox.removeAllItems();
        try {
            it = actualBranch.getCommits();
        } catch (GitException | IOException gitException) {
            GUIController.getInstance().errorHandler(gitException);
        }
        int count = 0;
        while (it.hasNext()) {
            commits.add(count, it.next());
            count++;
        }
        for (GitCommit commit : commits) {
            commitComboBox.addItem(commit.getHashAbbrev() + " - " + commit.getMessage());
        }
    }
}