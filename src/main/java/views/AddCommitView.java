package views;

import commands.Add;
import commands.Commit;
import controller.GUIController;
import git.GitData;
import git.GitFile;
import git.GitStatus;
import git.exception.GitException;
import settings.Settings;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;

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


  /**
   * At the time of its instantiation, an instance of AddCommitView creates instances of all supported commands.
   * Concurrently, all button listeners are being configured, als well as the list of files with uncommittedchanges in
   * the middle panel
   */
  public AddCommitView() {
    c = GUIController.getInstance();
    gitData = new GitData();
    gitStatus = gitData.getStatus();
    settings = Settings.getInstance();

    //if cancelButton was pressed, open confirmation dialog whether current state of staging-area should be saved
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //ask whether the user wants to save the files in the staging-area
        int saveChanges = JOptionPane.showConfirmDialog(null,"Sollen die Änderungen an der Staging-Area gespeichert werden?",
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
        executeCommit(false);
        c.restoreDefaultView();
      }
    });


    //if amend button was pressed, perform git commit --amend
    amendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        executeAdd();
        executeCommit(true);
        c.restoreDefaultView();
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
    }
    catch (GitException | IOException e){
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
    commitMessageTextArea.setText(DEFAULT_COMMIT_MESSAGE);
    //when the user clicks inside the text area, the default message should disappear
    commitMessageTextArea.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        //TODO: geht das noch besser?
        JTextArea source = (JTextArea) e.getSource();
        String newText = "";
        if (!source.getText().contains(DEFAULT_COMMIT_MESSAGE)){
          newText = source.getText();
        }
        source.setText(newText);
      }

      //if the user has not added any text, restore the default message once the focus disappears
      @Override
      public void focusLost(FocusEvent e){
        JTextArea source = (JTextArea) e.getSource();
        String newText = DEFAULT_COMMIT_MESSAGE;
        if (!source.getText().isEmpty()){
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

  /**
   *
   * @return The JPanel that holds all the elements in the view
   */
  public JPanel getView() {
    return addCommitView;
  }

  /**
   * Returns the JPanel that holds all the elements in the view, sets the initial text of the commit message
   * text area to the specified String
   * @param commitMessage The message that should be presented in the commit message text area
   * @return The JPanel that holds all the elements in the view
   */
  public JPanel getView(String commitMessage){
    commitMessageTextArea.setText(commitMessage);
    return addCommitView;
  }

  /**
   * Updates the view. Here: does nothing
   */
  public void update() {
    //TODO: delete?
  }

  /**
   *
   * @return The default commit message that is presented initially in the commit message text area
   */
  public static String getDEFAULT_COMMIT_MESSAGE(){
    return DEFAULT_COMMIT_MESSAGE;
  }

  /*
  Passes all selected files to the add command and invokes the execution of the command 
   */
  private boolean executeAdd(){
    addCommand = new Add();
    boolean success = false;

    List<GitFile> filesToBeAdded = new LinkedList<>();

    //there are three different lists that may contain selected files: Unify them first
    List<FileListItem> selectedValuesList = newFilesList.getSelectedValuesList();
    selectedValuesList.addAll(modifiedChangedFilesList.getSelectedValuesList());
    selectedValuesList.addAll(deletedFilesList.getSelectedValuesList());

    for (FileListItem item : selectedValuesList){
      filesToBeAdded.add(item.getGitFile());
    }

    //pass all selected GitFiles to add
    addCommand.addFiles(filesToBeAdded);

    //execute git add
    success = addCommand.execute();
    return success;
  }

  /*
  invokes the commit command when the user clicks on the commit button
   */
  private boolean executeCommit(boolean amend){
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

  /*
  creates the lists in the middle panel which present all files with uncommitted changes. There are three lists
  which invoke this method: newFilesList, modifiedChangedFilesList and deletedFilesList.
  This method sets up the list model with the given files and adds a MouseListener to all of them. The mouse listener
  does two things: 1. it selects/ deselects the checkboxes of each item. 2. it selects one list element at a time whose
  diff to the current index (e.g. HEAD) is presented in the panel on the right
   */

  private void setUpFileList(JList<FileListItem> list, List<GitFile> files){
    //set the renderer that presents each item as a checkbox
    renderer = new FileListRenderer();
    list.setCellRenderer(renderer);

    //fill up the list with the given files
    DefaultListModel<FileListItem> defaultListModel = new DefaultListModel();
    List<FileListItem> fileListItems = new LinkedList<>();
    for (GitFile file : files){
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
   * This class defines the renderer for the list of files with uncommitted changes that is located in the middle
   * panel of AddCommitView. The renderer is configured to show items as checkboxes.
   */
  class FileListRenderer implements ListCellRenderer<FileListItem> {
    /**
     * Method that returns a Component that defines the appearance of the list entries
     * @param list The corresponding list the renderer is set for
     * @param value The value of the list entry, held by the list model
     * @param index The index of the list entry in the list
     * @param isSelected Whether the entry has been selected.
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
      if(value.getGitFile().isStaged()){
        checkBox.setForeground(Color.GREEN);
      }
      else{
        checkBox.setForeground(Color.RED);
      }

      //checkBox.setFocusPainted(cellHasFocus) does not work. This is a workaround to mark selected cell
      if (cellHasFocus){
        checkBox.setBackground(Color.LIGHT_GRAY);
      }
      return checkBox;
    }


  }

  /*
   * This class represents a list item that holds a GitFile instance. This class is necessary to build the list
   * of files with uncommitted changes that is located in the middle panel of AddCommitView.
   */
  class FileListItem {
    private GitFile gitFile;
    private boolean isSelected;
    private int i;

    /*
     * Creates a list item that holds a GitFile. When the file is already in the staging area,
     * the list item is initially set to be selected.
     * @param gitFile
     */
    FileListItem(GitFile gitFile){
      this.gitFile = gitFile;
      this.i = i;
      this.isSelected = gitFile.isStaged();
    }

    /*
     * Returns the selection state of the item
     */
    boolean isSelected(){
      return isSelected;
    }

    /*
     * Setter for the selection state. When the user clicks on a list entry, its selection state will change
     */
    void setSelected(boolean isSelected){
      this.isSelected = isSelected;
    }

    /*
     * Method to retrieve the GitFile instance that is encapsulated in the list item
     */
    GitFile getGitFile(){
      return gitFile;
    }

  }


}
