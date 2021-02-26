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
import java.awt.event.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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


  public AddCommitView() {

    //if cancelButton was pressed, open confirmation dialog whether current state of staging-area should be saved
    cancelButton.addActionListener(e -> {
      boolean close = true;
      if (!getFilesToBeAdded().isEmpty()){
        //ask whether the user wants to save the files in the staging-area
        int saveChanges = JOptionPane.showConfirmDialog(null, "Sollen die Änderungen an der Staging-Area gespeichert werden?",
                "Änderungen speichern", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        //if Yes was selected, perform git add on the files
        if (saveChanges == 0) {
          executeAdd();
        }
        //do not restore default view if user selected Cancel
        else if (saveChanges == 2){
          close = false;
        }
      }
      if(close){
        GUIController.getInstance().restoreDefaultView();
      }

    });

    //if commit Button was pressed, perform git add and git commit and restore default view if successful
    commitButton.addActionListener(e -> {
      executeAdd();
      if (executeCommit(false)) {
        GUIController.getInstance().restoreDefaultView();
      }
    });


    //if amend button was pressed, perform git commit --amend and restore default view if successful
    amendButton.addActionListener(e -> {
      executeAdd();
      if (executeCommit(true)) {
        GUIController.getInstance().restoreDefaultView();
      }
    });


    //set the default text of the commit message text area
    commitMessageTextArea.setText(DEFAULT_COMMIT_MESSAGE);
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

  public AddCommitView(String commitMessage) {
    this();
    commitMessageTextArea.setText(commitMessage);
  }

  /**
   * @return The JPanel that holds all the elements in the view
   */
  public JPanel getView() {
    return contentPanel;
  }

  /**
   * Updates the view.
   */
  public void update() {
    //does nothing
    //TODO: weg?
  }

  /*
  Passes all selected files to the add command and invokes the execution of the command
   */
  private void executeAdd() {
    Add addCommand = new Add();

    //pass all selected GitFiles to add
    List<GitFile> filesToBeAdded = getFilesToBeAdded();
    addCommand.setFiles(filesToBeAdded);

    //execute git add
    addCommand.execute();

  }

  private List<GitFile> getFilesToBeAdded(){
    List<GitFile> filesToBeAdded = new LinkedList<>();

    //iterate over all three lists and extract the items with selected state
    filesToBeAdded.addAll(getSelectedGitFiles(newFilesList));
    filesToBeAdded.addAll(getSelectedGitFiles(modifiedChangedFilesList));
    filesToBeAdded.addAll(getSelectedGitFiles(deletedFilesList));

    return filesToBeAdded;
  }



  /*
  invokes the commit command when the user clicks on the commit button
   */
  private boolean executeCommit(boolean amend) {
    Commit commitCommand = new Commit();
    boolean success;

    //set amend and commit message
    commitCommand.setAmend(amend);
    commitCommand.setCommitMessage(commitMessageTextArea.getText());

    //execute git commit
    success = commitCommand.execute();
    return success;
  }

  /*
  creates the lists in the middle panel which present all files with uncommitted changes. There are three lists
  which invoke this method: newFilesList, modifiedChangedFilesList and deletedFilesList.
  This method sets up the list model with the given files and adds a MouseListener to all of them. The mouse listener
  does two things: 1. it selects/ deselects the checkboxes of each item. 2. it selects one list element at a time whose
  diff to the current index (e.g. HEAD) is presented in the panel on the right
  */
  private JList<FileListItem> setUpFileList(List<GitFile> files) {
    FileListItem [] values = new FileListItem[files.size()];
    int i = 0;

    for (GitFile gitFile : files){
      assert i < values.length;
      values[i] = new FileListItem(gitFile);
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
  Iterate over the given list of FileListItems and check which ones have selected state. Return the nested GitFile
  objects of those FileListItems with selected state.
   */
  private List<GitFile> getSelectedGitFiles(JList<FileListItem> list) {
    List<GitFile> selectedCheckboxes = new LinkedList<>();
    for (int i = 0; i < list.getModel().getSize(); i++) {
      FileListItem item = list.getModel().getElementAt(i);
      if (item.isSelected()) {
        selectedCheckboxes.add(item.getGitFile());
      }
    }
    return selectedCheckboxes;
  }

  private void createUIComponents() {
    GitData data = new GitData();
    GitStatus gitStatus = data.getStatus();
    List<GitFile> newFiles = new LinkedList<>();
    List<GitFile> modifiedChangedFiles = new LinkedList<>();
    List<GitFile> deletedFiles = new LinkedList<>();
    try{
      newFiles = gitStatus.getNewFiles();
      modifiedChangedFiles = gitStatus.getModifiedChangedFiles();
      deletedFiles = gitStatus.getDeletedFiles();
    } catch (IOException | GitException e) {
      GUIController controller = GUIController.getInstance();
      controller.restoreDefaultView();
      controller.errorHandler(e);
      //TODO: was passiert mit dem Fenster?
    }
    newFilesList = setUpFileList(newFiles);
    modifiedChangedFilesList = setUpFileList(modifiedChangedFiles);
    deletedFilesList = setUpFileList(deletedFiles);
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
  private static class FileListItem {
    private final GitFile gitFile;
    private boolean isSelected;

    /*
     * Creates a list item that holds a GitFile. When the file is already in the staging area,
     * the list item is initially set to be selected.
     * @param gitFile
     */
    FileListItem(GitFile gitFile) {
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
