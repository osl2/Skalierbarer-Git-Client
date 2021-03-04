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
import java.util.logging.Logger;

/**
 * Class of the DialogView from the command branch
 */
public class BranchDialogView implements IDialogView {
    private List<GitBranch> branches;
    private final LinkedList<GitCommit> commits = new LinkedList<>();
    @SuppressWarnings("unused")
    private JPanel panel1;
    private JPanel BranchPanel;
    private JComboBox<String> branchComboBox;
    private JComboBox<String> commitComboBox;
    private JTextField nameField;
    private JButton branchButton;
    @SuppressWarnings("unused")
    private JLabel branchLabel;
    @SuppressWarnings("unused")
    private JLabel commitLabel;
    @SuppressWarnings("unused")
    private JLabel nameLabel;


    /**
     * Constructor for a new Instance
     */
    public BranchDialogView() {
        branchComboBox.setName("branchComboBox");
        commitComboBox.setName("commitComboBox");
        nameField.setName("nameField");
        branchButton.setName("branchButton");
        commitComboBox.setRenderer(new BranchDialogRenderer());
        commitComboBox.setMaximumRowCount(6);
        // Adds the existing branches to the comboBox
        try {
            GitData gitData = new GitData();
            branches = gitData.getBranches();
        } catch (GitException e) {
            Logger.getGlobal().warning(e.getMessage());
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
    private static class BranchDialogRenderer extends JTextArea implements ListCellRenderer<String> {
        /**
         * Constructor for a new instance
         */
        public BranchDialogRenderer() {
            this.setLineWrap(true);
            this.setWrapStyleWord(true);
        }

        /**
         * Return a component that has been configured to display the specified
         * value. That component's <code>paint</code> method is then called to
         * "render" the cell.  If it is necessary to compute the dimensions
         * of a list because the list cells do not have a fixed size, this method
         * is called to generate a component on which <code>getPreferredSize</code>
         * can be invoked.
         *
         * @param list         The JList we're painting.
         * @param value        The value returned by list.getModel().getElementAt(index).
         * @param index        The cells index.
         * @param isSelected   True if the specified cell was selected.
         * @param cellHasFocus True if the specified cell has the focus.
         * @return A component whose paint() method will render the specified value.
         * @see JList
         * @see ListSelectionModel
         * @see ListModel
         */
        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            Color background = Color.WHITE;
            this.setText(value);
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
        while (true) {
            assert it != null;
            if (!it.hasNext()) break;
            commits.add(count, it.next());
            count++;
        }
        for (GitCommit commit : commits) {
            commitComboBox.addItem(commit.getHashAbbrev() + " - " + commit.getMessage());
        }
    }
}