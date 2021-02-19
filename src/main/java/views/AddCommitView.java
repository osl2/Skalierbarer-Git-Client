package views;

import commands.Add;
import commands.Commit;
import controller.GUIController;
import git.GitData;
import git.GitFile;
import git.GitStatus;
import git.exception.GitException;
import settings.Settings;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class AddCommitView extends JPanel implements IView {

  private JPanel addCommitView;
  private JPanel commitMessagePanel;
  private JTextArea commitMessageTextArea;
  private JPanel buttonPanel;
  private JButton cancelButton;
  private JButton commitButton;
  private JButton amendButton;
  private JPanel statusPanel;
  private JPanel diffPanel;
  private DiffView diffView;
  private JScrollPane modifiedChangedFilesScrollPane;
  private JList modifiedChangedFilesList;
  private JTextField modifiedChangedFilesTextField;
  private JScrollPane newFilesScrollPane;
  private JList newFilesList;
  private JTextField newFilesTextField;
  private JTextField deletedFilesTextField;
  private JList deletedFilesList;
  private JScrollPane deletedFilesScrollPane;
  private FileListRenderer renderer;
  private GUIController c;
  private Add addCommand;
  private Commit commitCommand;
  private GitStatus gitStatus;
  private GitData gitData;
  private Settings settings;
  private static final String DEFAULT_COMMIT_MESSAGE = "Hier die Commit-Nachricht eingeben";
  private String commitMessage;


  public AddCommitView() {
    this.commitMessage = DEFAULT_COMMIT_MESSAGE;
  }

  public AddCommitView(String commitMessage) {
    this.commitMessage = commitMessage;
  }

  /**
   * @return The JPanel that holds all the elements in the view
   */
  public JPanel getView() {

    buildAddCommitView();
    return addCommitView;
  }

  /**
   * Updates the view. Here: does nothing
   */
  public void update() {
  }

  /**
   * @return The default commit message that is presented initially in the commit message text area
   */
  public static String getDEFAULT_COMMIT_MESSAGE() {
    return DEFAULT_COMMIT_MESSAGE;
  }

  /*
   * This method is invoked inside getView(). It configures all button listeners as well as the lists of files
   * with uncommitted changes in the middle status panel. It sets up the diff panel and on the left and the
   * text area for commit messages on the right.
   */
  private void buildAddCommitView() {
    c = GUIController.getInstance();
    gitData = new GitData();
    gitStatus = gitData.getStatus();
    settings = Settings.getInstance();

    //if cancelButton was pressed, open confirmation dialog whether current state of staging-area should be saved
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //ask whether the user wants to save the files in the staging-area
        int saveChanges = JOptionPane.showConfirmDialog(null, "Sollen die Änderungen an der Staging-Area gespeichert werden?",
                "Änderungen speichern", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        //if yes or no was selected (else, do nothing)
        if (saveChanges != 2) {
          //if yes was selected, perform git add on the files. Restore default view to close AddCommitView
          if (saveChanges == 0) {
            executeAdd();
          }
          c.restoreDefaultView();
        }
      }
    });

    //if commit Button was pressed, perform git add and git commit and restore default view
    commitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        executeAdd();
        if (executeCommit(false)) {
          c.restoreDefaultView();
        }
      }
    });


    //if amend button was pressed, perform git commit --amend
    amendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        executeAdd();
        if (executeCommit(true)) {
          c.restoreDefaultView();
        }
      }
    });

    //set up the list for newly creates, modified and deleted files with the data from GitStatus
    List<GitFile> modifiedChangedFiles;
    List<GitFile> newFiles;
    List<GitFile> deletedFiles;
    try {
      modifiedChangedFiles = gitStatus.getModifiedChangedFiles();
      newFiles = gitStatus.getNewFiles();
      deletedFiles = gitStatus.getDeletedFiles();
    } catch (GitException | IOException e) {
      modifiedChangedFiles = new LinkedList<>();
      newFiles = new LinkedList<>();
      deletedFiles = new LinkedList<>();
      c.errorHandler(e);
    }

    //set up renderer, listeners and data model for all lists
    setUpFileList(newFilesList, newFiles);
    setUpFileList(modifiedChangedFilesList, modifiedChangedFiles);
    setUpFileList(deletedFilesList, deletedFiles);

    //set the default text of the commit message text area
    commitMessageTextArea.setText(commitMessage);
    //when the user clicks inside the text area, the default message should disappear
    commitMessageTextArea.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        //TODO: geht das noch besser?
        JTextArea source = (JTextArea) e.getSource();
        String newText = "";
        if (!source.getText().contains(DEFAULT_COMMIT_MESSAGE)) {
          newText = source.getText();
        }
        source.setText(newText);
      }

      //if the user has not added any text, restore the default message once the focus disappears
      @Override
      public void focusLost(FocusEvent e) {
        JTextArea source = (JTextArea) e.getSource();
        String newText = DEFAULT_COMMIT_MESSAGE;
        if (!source.getText().isEmpty()) {
          newText = source.getText();
        }
        source.setText(newText);
      }
    });

    //set up the diff view
    diffView = new DiffView();
    diffPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    diffPanel.add(diffView.openDiffView());
  }

  /*
  Passes all selected files to the add command and invokes the execution of the command
   */
  private boolean executeAdd() {
    addCommand = new Add();
    boolean success = false;

    List<GitFile> filesToBeAdded = new LinkedList<>();

    //iterate over all three lists and extract the items with selected state
    filesToBeAdded.addAll(getSelectedGitFiles(newFilesList));
    filesToBeAdded.addAll(getSelectedGitFiles(modifiedChangedFilesList));
    filesToBeAdded.addAll(getSelectedGitFiles(deletedFilesList));

    //pass all selected GitFiles to add
    addCommand.addFiles(filesToBeAdded);

    //execute git add
    success = addCommand.execute();
    return success;
  }

  /*
  creates the lists in the middle panel which present all files with uncommitted changes. There are three lists
  which invoke this method: newFilesList, modifiedChangedFilesList and deletedFilesList.
  This method sets up the list model with the given files and adds a MouseListener to all of them. The mouse listener
  does two things: 1. it selects/ deselects the checkboxes of each item. 2. it selects one list element at a time whose
  diff to the current index (e.g. HEAD) is presented in the panel on the right
   */

  /*
  invokes the commit command when the user clicks on the commit button
   */
  private boolean executeCommit(boolean amend) {
    commitCommand = new Commit();
    boolean success = false;

    //set amend and commit message
    commitCommand.setAmend(amend);
    String commitMessage = commitMessageTextArea.getText();
    commitCommand.setCommitMessage(commitMessage);

    //execute git commit
    success = commitCommand.execute();
    return success;
  }

  private void setUpFileList(JList<FileListItem> list, List<GitFile> files) {
    //set the renderer that presents each item as a checkbox
    renderer = new FileListRenderer();
    list.setCellRenderer(renderer);

    //fill up the list with the given files
    DefaultListModel<FileListItem> defaultListModel = new DefaultListModel();
    List<FileListItem> fileListItems = new LinkedList<>();
    for (GitFile file : files) {
      FileListItem item = new FileListItem(file);
      fileListItems.add(item);
    }
    defaultListModel.addAll(fileListItems);
    list.setModel(defaultListModel);

    //only one element can be chosen at a time
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    //Mouse Listener for Checkboxes. Selected files are being marked for git add
    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        JList list = (JList) event.getSource();
        //get the item index that was clicked
        int index = list.locationToIndex(event.getPoint());
        FileListItem item = (FileListItem) list.getModel().getElementAt(index);
        //set index in list to be selected
        list.setSelectedIndex(index);
        //call diff on selected file
        diffView.setDiff(item.getGitFile());
        //if item was not selected before, select; otherwise, deselect
        item.setSelected(!item.isSelected());
        //repaint cell
        list.repaint(list.getCellBounds(index, index));

      }
    });

  }

  /*
  Iterate over the given list of FileListItems and check which ones have selected state. Return the nested GitFile
  objects of those FileListItems with selected state.
   */
  private List<GitFile> getSelectedGitFiles(JList<FileListItem> list) {
    List<GitFile> selectedCheckboxes = new LinkedList<>();
    for (int i = 0; i < list.getModel().getSize(); i++) {
      FileListItem item = (FileListItem) list.getModel().getElementAt(i);
      if (item.isSelected()) {
        selectedCheckboxes.add(item.getGitFile());
      }
    }
    return selectedCheckboxes;
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
    addCommitView = new JPanel();
    addCommitView.setLayout(new BorderLayout(0, 0));
    addCommitView.setBackground(new Color(-12828863));
    addCommitView.setEnabled(true);
    addCommitView.setMinimumSize(new Dimension(1280, 720));
    commitMessagePanel = new JPanel();
    commitMessagePanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
    commitMessagePanel.setBackground(new Color(-12828863));
    commitMessagePanel.setFocusable(true);
    commitMessagePanel.setMinimumSize(new Dimension(427, -1));
    commitMessagePanel.setPreferredSize(new Dimension(427, -1));
    addCommitView.add(commitMessagePanel, BorderLayout.WEST);
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    buttonPanel.setMinimumSize(new Dimension(422, 240));
    buttonPanel.setPreferredSize(new Dimension(422, 240));
    commitMessagePanel.add(buttonPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    cancelButton = new JButton();
    cancelButton.setText("Zurück");
    buttonPanel.add(cancelButton);
    commitButton = new JButton();
    commitButton.setText("Commit");
    buttonPanel.add(commitButton);
    amendButton = new JButton();
    amendButton.setText("Amend");
    buttonPanel.add(amendButton);
    final JScrollPane scrollPane1 = new JScrollPane();
    commitMessagePanel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    commitMessageTextArea = new JTextArea();
    commitMessageTextArea.setEnabled(true);
    commitMessageTextArea.setMinimumSize(new Dimension(10, 170));
    commitMessageTextArea.setOpaque(true);
    commitMessageTextArea.setPreferredSize(new Dimension(400, 500));
    commitMessageTextArea.setText("");
    commitMessageTextArea.setToolTipText("Hier die Commit-Nachricht eingeben ");
    scrollPane1.setViewportView(commitMessageTextArea);
    statusPanel = new JPanel();
    statusPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
    statusPanel.setBackground(new Color(-12828863));
    statusPanel.setMinimumSize(new Dimension(427, -1));
    statusPanel.setPreferredSize(new Dimension(427, -1));
    addCommitView.add(statusPanel, BorderLayout.CENTER);
    modifiedChangedFilesScrollPane = new JScrollPane();
    statusPanel.add(modifiedChangedFilesScrollPane, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    modifiedChangedFilesList = new JList();
    modifiedChangedFilesScrollPane.setViewportView(modifiedChangedFilesList);
    modifiedChangedFilesTextField = new JTextField();
    modifiedChangedFilesTextField.setEditable(false);
    modifiedChangedFilesTextField.setText("Dateien, von denen schon eine ältere Version existiert");
    modifiedChangedFilesTextField.setToolTipText("Aktiviere das Kontrollkästchen, um die Datei der Staging-Area hinzuzufügen");
    statusPanel.add(modifiedChangedFilesTextField, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    newFilesScrollPane = new JScrollPane();
    statusPanel.add(newFilesScrollPane, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    newFilesList = new JList();
    newFilesScrollPane.setViewportView(newFilesList);
    newFilesTextField = new JTextField();
    newFilesTextField.setEditable(false);
    newFilesTextField.setText("Dateien, die neu erstellt wurden");
    newFilesTextField.setToolTipText("Aktiviere das Kontrollkästchen, um die Datei der Staging-Area hinzuzufügen");
    statusPanel.add(newFilesTextField, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    deletedFilesScrollPane = new JScrollPane();
    statusPanel.add(deletedFilesScrollPane, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    deletedFilesList = new JList();
    deletedFilesScrollPane.setViewportView(deletedFilesList);
    deletedFilesTextField = new JTextField();
    deletedFilesTextField.setEditable(false);
    deletedFilesTextField.setText("Gelöschte Dateien");
    deletedFilesTextField.setToolTipText("");
    statusPanel.add(deletedFilesTextField, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    diffPanel = new JPanel();
    diffPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    diffPanel.setBackground(new Color(-1));
    diffPanel.setFocusable(false);
    diffPanel.setMinimumSize(new Dimension(427, -1));
    diffPanel.setPreferredSize(new Dimension(427, -1));
    addCommitView.add(diffPanel, BorderLayout.EAST);
    diffPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return addCommitView;
  }

  /*
   * This class defines the renderer for the list of files with uncommitted changes that is located in the middle
   * panel of AddCommitView. The renderer is configured to show items as checkboxes.
   */
  private class FileListRenderer implements ListCellRenderer<FileListItem> {
    /**
     * Method that returns a Component that defines the appearance of the list entries
     *
     * @param list         The corresponding list the renderer is set for
     * @param value        The value of the list entry, held by the list model
     * @param index        The index of the list entry in the list
     * @param isSelected   Whether the entry has been selected.
     * @param cellHasFocus Whether the specific cell has focus
     * @return Component - in this case, a JCheckbox element
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends FileListItem> list, FileListItem value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
      JCheckBox checkBox = new JCheckBox();
      checkBox.setEnabled(list.isEnabled());
      checkBox.setSelected(value.isSelected());
      checkBox.setFont(list.getFont());
      checkBox.setBackground(list.getBackground());
      checkBox.setForeground(list.getForeground());
      checkBox.setText(value.getGitFile().getPath().getName());

      //color staged files in green, unstaged files in red
      if (value.getGitFile().isStaged()) {
        checkBox.setForeground(Color.GREEN);
      } else {
        checkBox.setForeground(Color.RED);
      }

      //checkBox.setFocusPainted(cellHasFocus) does not work. This is a workaround to mark selected cell
      if (cellHasFocus) {
        checkBox.setBackground(Color.LIGHT_GRAY);
      }
      return checkBox;
    }


  }

  /*
   * This class represents a list item that holds a GitFile instance. This class is necessary to build the list
   * of files with uncommitted changes that is located in the middle panel of AddCommitView.
   */
  private class FileListItem {
    private GitFile gitFile;
    private boolean isSelected;
    private int i;

    /*
     * Creates a list item that holds a GitFile. When the file is already in the staging area,
     * the list item is initially set to be selected.
     * @param gitFile
     */
    FileListItem(GitFile gitFile) {
      this.gitFile = gitFile;
      this.i = i;
      this.isSelected = gitFile.isStaged();
    }

    /*
     * Returns the selection state of the item
     */
    boolean isSelected() {
      return isSelected;
    }

    /*
     * Setter for the selection state. When the user clicks on a list entry, its selection state will change
     */
    void setSelected(boolean isSelected) {
      this.isSelected = isSelected;
    }

    /*
     * Method to retrieve the GitFile instance that is encapsulated in the list item
     */
    GitFile getGitFile() {
      return gitFile;
    }

  }


}
