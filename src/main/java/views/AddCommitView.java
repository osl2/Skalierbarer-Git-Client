package views;

import commands.Add;
import commands.Commit;
import controller.GUIController;
import git.GitData;
import git.GitFile;
import git.GitStatus;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddCommitView extends JPanel implements IView {

  public static final String DEFAULT_COMMIT_MESSAGE = "Hier Commit-Nachricht eingeben";
  private JPanel contentPanel;
  @SuppressWarnings("unused")
  private JPanel commitMessagePanel;
  private JTextArea commitMessageTextArea;
  @SuppressWarnings("unused")
  private JPanel buttonPanel;
  private JButton cancelButton;
  private JButton commitButton;
  private JButton amendButton;
  @SuppressWarnings("unused")
  private JPanel statusPanel;
  @SuppressWarnings("unused")
  private JPanel diffPanel;
  @SuppressWarnings("unused")
  private DiffView diffView;
  @SuppressWarnings("unused")
  private JScrollPane modifiedChangedFilesScrollPane;
  private JList<FileListItem> modifiedChangedFilesList;
  @SuppressWarnings("unused")
  private JTextField modifiedChangedFilesTextField;
  @SuppressWarnings("unused")
  private JScrollPane newFilesScrollPane;
  private JList<FileListItem> newFilesList;
  @SuppressWarnings("unused")
  private JTextField newFilesTextField;
  @SuppressWarnings("unused")
  private JTextField deletedFilesTextField;
  private JList<FileListItem> deletedFilesList;
  @SuppressWarnings("unused")
  private JScrollPane deletedFilesScrollPane;
  @SuppressWarnings("unused")
  private JScrollPane diffScrollPane;
  @SuppressWarnings("unused")
  private JTextPane diffTextPane;
  private final JList<FileListItem>[] statusListArray;
  private boolean amend;

  public AddCommitView() {
    //add all three types of lists to the status array
    statusListArray = new JList[3];
    statusListArray[0] = newFilesList;
    statusListArray[1] = modifiedChangedFilesList;
    statusListArray[2] = deletedFilesList;

    //if cancelButton was pressed, open confirmation dialog whether current state of staging-area should be saved
    cancelButton.addActionListener(e -> {
      boolean close = true;
      //ask whether the user wants to save his/her changes in the staging-area (if existent)
      if (!getFilesStatusChanged().isEmpty()) {
        int saveChanges = JOptionPane.showConfirmDialog(null, "Sollen die Änderungen an der Staging-Area gespeichert werden?",
                "Änderungen speichern", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        //if Yes was selected, perform git add on the files
        if (saveChanges == 0) {
          executeAdd();
        }
        //do not restore default view if user selected Cancel
        else if (saveChanges == 2) {
          close = false;
        }
      }
      if(close){
        GUIController.getInstance().restoreDefaultView();
      }

    });

    //if commit Button was pressed, perform git add and git commit and restore default view if successful
    commitButton.addActionListener(e -> {
      amend = false;
      executeAdd();
      executeCommit();
    });


    //if amend button was pressed, perform git commit --amend and restore default view if successful
    amendButton.addActionListener(e -> {
      amend = true;
      executeAdd();
      executeCommit();
    });

    //set the default text of the commit message text area
    String storedCommitMessage = new GitData().getStoredCommitMessage();
    commitMessageTextArea.setText(Objects.requireNonNullElse(storedCommitMessage, DEFAULT_COMMIT_MESSAGE));
    //when the user clicks inside the text area, the default message should disappear
    commitMessageTextArea.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        JTextArea source = (JTextArea) e.getSource();
        String newText = "";
        //do not reset the text in case the user has already entered something
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

    setNameComponents();

  }


  public AddCommitView(String commitMessage) {
    this();
    commitMessageTextArea.setText(commitMessage);
  }

  /**
   * @return The JPanel that holds all the elements in the view
   */
  @Override
  public JPanel getView() {
    return contentPanel;
  }

  /**
   * Updates the view.
   */
  @Override
  public void update() {
    createUIComponents();
    //set the default text of the commit message text area
    commitMessageTextArea.setText(DEFAULT_COMMIT_MESSAGE);
  }

  /*
   * Sets the names of the components that are being tested in AddCommitViewTest. DO NOT CHANGE, otherwise,
   * tests might fail
   */
  private void setNameComponents() {
    modifiedChangedFilesList.setName("modifiedChangedFilesList");
    newFilesList.setName("newFilesList");
    deletedFilesList.setName("deletedFilesList");
    commitMessageTextArea.setName("commitMessageTextArea");
    cancelButton.setName("cancelButton");
    commitButton.setName("commitButton");
    amendButton.setName("amendButton");
  }

  /*
  Passes all selected files to the add command and invokes the execution of the command
   */
  private void executeAdd() {
    Add addCommand = new Add();


    //pass all selected GitFiles to add
    addCommand.setFiles(getSelectedGitFiles());

    //execute git add and set command line
    if (addCommand.execute()) {
      GUIController.getInstance().setCommandLine(addCommand.getCommandLine());
    }

  }

  /*
  invokes the commit command when the user clicks on the commit button
   */
  private void executeCommit() {

    //if staging area is not empty, show confirmation dialog
    List<GitFile> stagedFiles = getStagedFiles();
    if (!stagedFiles.isEmpty()) {
      StringBuilder message = new StringBuilder();
      message.append("Bist du sicher, dass die Änderungen an folgenden Dateien eingebucht werden sollen?\n");
      for (GitFile gitFile : stagedFiles) {
        message.append(gitFile.getPath().getName());
        message.append("\n");
      }
      int confirmation = JOptionPane.showConfirmDialog(null, message.toString(),
              "Änderungen einbuchen?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (confirmation != 0) {
        return;
      }
    }

    Commit commitCommand = new Commit();

    //set amend and commit message
    commitCommand.setAmend(amend);
    commitCommand.setCommitMessage(commitMessageTextArea.getText());

    //execute git commit
    if (commitCommand.execute()) {
      GUIController controller = GUIController.getInstance();
      controller.setCommandLine(commitCommand.getCommandLine());
      controller.restoreDefaultView();
    }
  }

  /*
   * Get all files whose status changed from Add, depending on the currently selected files
   */
  private List<GitFile> getFilesStatusChanged() {
    Add add = new Add();
    add.setFiles(getSelectedGitFiles());
    return add.getFilesStatusChanged();
  }

  /*
  Get all files that are currently in the staging area
   */
  private List<GitFile> getStagedFiles() {
    GitData gitData = new GitData();
    GitStatus gitStatus = gitData.getStatus();
    List<GitFile> stagedFiles = new ArrayList<>();
    try {
      stagedFiles = gitStatus.getStagedFiles();
    } catch (IOException | GitException e) {
      GUIController.getInstance().errorHandler(e);
    }
    return stagedFiles;
  }

  /*
  creates the lists in the middle panel which present all files with uncommitted changes. There are three lists
  which invoke this method: newFilesList, modifiedChangedFilesList and deletedFilesList.
  This method sets up the list model with the given files and adds a MouseListener to all of them. The mouse listener
  does two things: 1. it selects/ deselects the checkboxes of each item. 2. it selects one list element at a time whose
  diff to the current index (e.g. HEAD) is presented in the panel on the right
  */
  private JList<FileListItem> setUpFileList(List<GitFile> files) {
    FileListItem[] values = new FileListItem[files.size()];
    int i = 0;

    for (GitFile gitFile : files){
      assert i < values.length;
      try {
        values[i] = new FileListItem(gitFile);
      } catch (GitException e) {
        GUIController.getInstance().errorHandler(e);
        //TODO
      }
      i++;
    }

    JList<FileListItem> list = new JList<>(values);

    //set the renderer that presents each item as a checkbox
    FileListRenderer renderer = new FileListRenderer();
    list.setCellRenderer(renderer);

    //only one element can be chosen at a time
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    //Mouse for Checkboxes. Selected files are being marked for git add


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



    return list;
  }

  /*
  Iterate over all jlists in the status panel and check which FileListItems have selected state. Return the nested GitFile
  objects of those FileListItems with selected state.
   */
  private List<GitFile> getSelectedGitFiles() {
    List<GitFile> selectedCheckboxes = new ArrayList<>();
    for (JList<FileListItem> list : statusListArray) {
      for (int i = 0; i < list.getModel().getSize(); i++) {
        FileListItem item = list.getModel().getElementAt(i);
        if (item.isSelected()) {
          selectedCheckboxes.add(item.getGitFile());
        }
      }
    }
    return selectedCheckboxes;
  }

  @SuppressWarnings("unused")
  private void createUIComponents() {
    GitData data = new GitData();
    GitStatus gitStatus = data.getStatus();
    List<GitFile> newFiles = new ArrayList<>();
    List<GitFile> modifiedChangedFiles = new ArrayList<>();
    List<GitFile> deletedFiles = new ArrayList<>();
    try {
      newFiles = gitStatus.getNewFiles();
      modifiedChangedFiles = gitStatus.getModifiedChangedFiles();
      deletedFiles = gitStatus.getDeletedFiles();
    } catch (IOException | GitException e) {
      GUIController controller = GUIController.getInstance();
      //this is very bad for our view, go back to default view and show error message 
      controller.restoreDefaultView();
      controller.errorHandler(e);
    }
    newFilesList = setUpFileList(newFiles);
    modifiedChangedFilesList = setUpFileList(modifiedChangedFiles);
    deletedFilesList = setUpFileList(deletedFiles);

    //set up the diff view
    diffView = new DiffView();
    diffTextPane = diffView.openDiffView();
  }

  /*
   * This class defines the renderer for the list of files with uncommitted changes that is located in the middle
   * panel of AddCommitView. The renderer is configured to show items as checkboxes.
   */
  private static class FileListRenderer implements ListCellRenderer<FileListItem> {
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
      try {
        if (value.getGitFile().isStaged()) {
          checkBox.setForeground(Color.GREEN);
        } else {
          checkBox.setForeground(Color.RED);
        }
      } catch (GitException e) {
        GUIController.getInstance().errorHandler(e);
        //TODO
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
  static class FileListItem {
    private final GitFile gitFile;
    private boolean isSelected;

    /*
     * Creates a list item that holds a GitFile. When the file is already in the staging area,
     * the list item is initially set to be selected.
     * @param gitFile
     */
    FileListItem(GitFile gitFile) throws GitException {
      this.gitFile = gitFile;
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
