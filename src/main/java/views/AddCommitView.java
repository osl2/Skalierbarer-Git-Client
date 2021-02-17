package views;

import commands.Add;
import commands.Commit;
import controller.GUIController;
import git.GitData;
import git.GitFile;
import git.GitStatus;
import git.exception.GitException;
import settings.Settings;
import views.IView;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AddCommitView extends JPanel implements IView {

  private JPanel addCommitView;
  private JPanel commitMessagePanel;
  private JTextArea commitMessageTextArea;
  private JPanel buttonPanel;
  private JButton cancelButton;
  private JButton commitButton;
  private JButton amendButton;
  private JPanel statusPanel;
  private JList<FileListItem> stagedFilesList;
  private JPanel diffPanel;
  private DiffView diffView;
  private JScrollPane stagedFilesScrollPane;
  private JScrollPane unstagedFilesScrollPane;
  private JList unstagedFilesList;
  private JTextField stagedFilesTextField;
  private JTextField unstagedFilesTextField;
  private JTextField testTextField;
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
   * Concurrently, all button listeners are being configured.
   */
  public AddCommitView() {
    c = GUIController.getInstance();
    gitData = new GitData();
    addCommand = new Add();
    commitCommand = new Commit();
    gitStatus = gitData.getStatus();
    settings = Settings.getInstance();

    //if cancelButton was pressed, open confirmation dialog whether current state of staging-area should be saved
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //ask whether the user wants to save the files in the staging-area
        //parameters 3-5 are optional
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

    //set up the list for staged files and unstaged files by retrieving the data from GitStatus
    List<GitFile> unstagedFiles;
    List<GitFile> stagedFiles;
    try {
      stagedFiles = gitStatus.getStagedFiles();
      unstagedFiles = gitStatus.getUnstagedFiles();
    }
    catch (GitException | IOException e){
      unstagedFiles = new LinkedList<>();
      stagedFiles = new LinkedList<>();
      c.errorHandler(e);
    }

    //set up renderer and data model for both lists
    setUpFileList(stagedFilesList, stagedFiles);
    setUpFileList(unstagedFilesList, unstagedFiles);

    //make all files appear green or red, depending on their current stage
    stagedFilesList.setForeground(Color.GREEN);
    unstagedFilesList.setForeground(Color.RED);

    commitMessageTextArea.setText(DEFAULT_COMMIT_MESSAGE);
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

    //set up diff view
    diffView = new DiffView();
    diffPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    diffPanel.add(diffView.openDiffView());
  }

  public JPanel getView() {
    return addCommitView;
  }

  public JPanel getView(String commitMessage){
    commitMessageTextArea.setText(commitMessage);
    return addCommitView;
  }


  public void update() {
    //TODO: wird die überhaupt benötigt?
  }

  private void createUIComponents() {
    stagedFilesList = new JList<FileListItem>();
    unstagedFilesList = new JList<FileListItem>();
    commitMessageTextArea = new JTextArea();

    //TODO: diff area
    diffPanel = new JPanel();
  }

  private boolean executeAdd(){
    boolean success = false;

    //pass all selected FileListItems to add
    List<GitFile> filesToBeAdded = new LinkedList<>();

    for (FileListItem item : stagedFilesList.getSelectedValuesList()){
      filesToBeAdded.add(item.getGitFile());
    }


    addCommand.addFiles(filesToBeAdded);

    //execute git add
    success = addCommand.execute();
    return success;
  }

  private boolean executeCommit(boolean amend){
    boolean success = false;

    commitCommand.setAmend(amend);
    String commitMessage = commitMessageTextArea.getText();
    commitCommand.setCommitMessage(commitMessage);

    //execute git commit
    success = commitCommand.execute();
    return success;
  }

  //creates the list in the middle panel that presents all files with uncommitted changes
  private void setUpFileList(JList<FileListItem> list, List<GitFile> files){
    renderer = new FileListRenderer();
    list.setCellRenderer(renderer);
    DefaultListModel<FileListItem> defaultListModel = new DefaultListModel();


    List<FileListItem> fileListItems = new LinkedList<>();
    for (GitFile file : files){
      FileListItem item = new FileListItem(file);
      fileListItems.add(item);
    }

    defaultListModel.addAll(fileListItems);
    list.setModel(defaultListModel);
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

    /*
    //List selection listener for a single item. Call git diff on the selected item
    list.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        JList<FileListItem> list = (JList<FileListItem>) e.getSource();
        List<FileListItem> selectedValuesList = list.getSelectedValuesList();
        assert selectedValuesList.size() <= 1;
        for (FileListItem item : selectedValuesList) {
          diffView.setDiff(item.gitFile);
        }
      }
    });

     */





  }

  /*
   * This class defines the renderer for the list of files with uncommitted changes that is located in the middle
   * panel of AddCommitView.
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
      //checkBox.setFocusPainted(cellHasFocus) does not work. This is a workaround to mark selected cell
      if (cellHasFocus){
        checkBox.setBackground(Color.LIGHT_GRAY);
      }
      return checkBox;
    }


  }

  /*
   * This class represents a list item that holds a GitFile instance. This class is needed to build the list
   * of files with uncommitted changes that is located in the middle panel of AddCommitView.
   * @see AddCommitView
   */
  class FileListItem {
    private GitFile gitFile;
    private boolean isSelected;
    private int i;

    /*
     * Creates a list item that holds a GitFile instance. When the file is already in the staging area,
     * the list item is initialized to be selected.
     * @param gitFile
     */
    FileListItem(GitFile gitFile){
      this.gitFile = gitFile;
      this.i = i;
      this.isSelected = gitFile.isStaged();
    }

    /*
     *
     * @return The selection state of the item
     */
    boolean isSelected(){
      return isSelected;
    }

    /*
     * Setter for the selection state. When the user clicks on a list entry, its selection state will change
     * @param isSelected True, if the item should be selected (i.e., the nested GitFile should be marked for git add)
     */
    void setSelected(boolean isSelected){
      this.isSelected = isSelected;
    }

    /*
     * Method to retrieve the GitFile instance from the list item
     * @return GitFile - the instance that is encapsulated in the list item
     */
    GitFile getGitFile(){
      return gitFile;
    }

  }

  public static String getDEFAULT_COMMIT_MESSAGE(){
    return DEFAULT_COMMIT_MESSAGE;
  }
}
