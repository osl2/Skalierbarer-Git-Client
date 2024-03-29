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

/**
 * The View opened to add files to the staging area and to commit files.
 */
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
  private JCheckBox modifiedChangedFilesCheckBox;
  private JCheckBox newFilesCheckBox;
  private JCheckBox deletedFilesCheckBox;
  private final JList<FileListItem>[] statusListArray;
  private boolean amend;

  /**
   * Create a new View.
   */
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
        if (saveChanges == JOptionPane.YES_OPTION) {
          executeAdd();
        }
        //do not restore default view if user selected Cancel
        else if (saveChanges == JOptionPane.CANCEL_OPTION) {
          close = false;
        }
      }
      if(close){
        GUIController.getInstance().restoreDefaultView();

        //save the commit message if user closes the view
        GitData data = new GitData();
        if (commitMessageTextArea.getText().compareTo(DEFAULT_COMMIT_MESSAGE) != 0) {
          data.writeStoredCommitMessage(commitMessageTextArea.getText());
        }
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

    modifiedChangedFilesCheckBox.addItemListener(e -> {
      JCheckBox source = (JCheckBox) e.getSource();
      boolean selected = source.isSelected();
      selectAll(modifiedChangedFilesList, selected);
    });

    newFilesCheckBox.addItemListener(e -> {
      JCheckBox source = (JCheckBox) e.getSource();
      boolean selected = source.isSelected();
      selectAll(newFilesList, selected);
    });
    setNameComponents();

    deletedFilesCheckBox.addItemListener(e -> {
      JCheckBox source = (JCheckBox) e.getSource();
      boolean selected = source.isSelected();
      selectAll(deletedFilesList, selected);
    });
  }

  /**
   * This constructor loads the view with the passed commit message
   *
   * @param commitMessage The message that should be displayed in the commitMessageTextArea
   */
  public AddCommitView(String commitMessage) {
    this();
    commitMessageTextArea.setText(commitMessage);
  }

  @Override
  public JPanel getView() {
    return contentPanel;
  }

  @Override
  public void update() {
    createUIComponents();
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
    modifiedChangedFilesCheckBox.setName("modifiedChangedFilesCheckBox");
    newFilesCheckBox.setName("newFilesCheckBox");
    deletedFilesCheckBox.setName("deletedFilesCheckBox");
    diffTextPane.setName("diffTextPane");
  }

  /*
  Passes all selected files to the add command and invokes the execution of the command
   */
  private void executeAdd() {
    Add addCommand = new Add();


    //pass all selected GitFiles to add
    addCommand.setSelectedFiles(getSelectedGitFiles());

    //execute git add and set command line
    if (addCommand.execute()) {
      GUIController.getInstance().setCommandLine(addCommand.getCommandLine());
    }

  }

  /*
  invokes the commit command when the user clicks on the commit button
   */
  private void executeCommit() {
    Commit commitCommand = new Commit();

    //set amend and commit message
    commitCommand.setAmend(amend);
    commitCommand.setCommitMessage(commitMessageTextArea.getText());

    //if staging area is not empty, show confirmation dialog
    List<GitFile> stagedFiles = getStagedFiles();
    if (!stagedFiles.isEmpty() || amend) {
      int confirmation = JOptionPane.showConfirmDialog(null,
              getCommitConfirmationPane(stagedFiles, commitCommand.getCommitMessage()), "Änderungen einbuchen?", JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE);
      if (confirmation != JOptionPane.YES_OPTION) {
        return;
      }
    }

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
    add.setSelectedFiles(getSelectedGitFiles());
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
  Builds the panel that asks the user whether he/ she for confirmation when trying to commit
   */
  private JPanel getCommitConfirmationPane(List<GitFile> stagedFiles, String commitMessage) {
    JPanel panel = new JPanel(new BorderLayout());
    JTextField headerTextField = new JTextField("Bist du sicher, dass die Änderungen an folgenden Dateien eingebucht werden sollen?");
    headerTextField.setEditable(false);
    JTextArea messageTextArea = new JTextArea("Commit-Nachricht: " + commitMessage);
    messageTextArea.setEditable(false);
    messageTextArea.setLineWrap(true);
    JScrollPane messageScrollPane = new JScrollPane(messageTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    messageScrollPane.setPreferredSize(new Dimension(500, 100));
    messageScrollPane.setMaximumSize(new Dimension(500, 200));

    StringBuilder message = new StringBuilder();
    for (GitFile gitFile : stagedFiles) {
      message.append(gitFile.getPath().getName());
      message.append("\n");
    }
    JTextArea textArea = new JTextArea(message.toString());
    textArea.setEditable(false);
    JScrollPane fileScrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    textArea.setLineWrap(true);
    fileScrollPane.setPreferredSize(new Dimension(500, 100));
    fileScrollPane.setMaximumSize(new Dimension(500, 200));

    panel.add(headerTextField, BorderLayout.NORTH);
    panel.add(messageScrollPane, BorderLayout.SOUTH);
    panel.add(fileScrollPane, BorderLayout.CENTER);
    return panel;
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
        Point eventPoint = event.getPoint();

        //get the index closest to the clicking point
        int index = list.locationToIndex(eventPoint);

        //get the rectangle of all cells in the list
        Rectangle validListRectangle = new Rectangle(0, 0);
        if (list.getModel().getSize() > 0) {
          validListRectangle = list.getCellBounds(0, list.getModel().getSize() - 1);
        }

        //if index is -1, list is empty. Otherwise, eventPoint might be outside the list rectangle
        if (index < 0 || eventPoint.x > validListRectangle.width || eventPoint.y > validListRectangle.height) {
          return;
        }
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

  // Method to select/deselect all items in a list
  private void selectAll(JList<FileListItem> list, boolean select) {

    for (int i = 0; i < list.getModel().getSize(); i++) {
      FileListItem item = list.getModel().getElementAt(i);
      item.setSelected(select);
    }
    //repaint list
    list.repaint();
  }

  /**
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
        if (value.isSelected()) {
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

  /**
   * This class represents a list item that holds a GitFile instance. This class is necessary to build the list
   * of files with uncommitted changes that is located in the middle panel of AddCommitView.
   */
  protected static class FileListItem {
    private final GitFile gitFile;
    private boolean isSelected;

    /**
     * Creates a list item that holds a GitFile. When the file is already in the staging area,
     * the list item is initially set to be selected.
     * @param gitFile the File to show
     */
    FileListItem(GitFile gitFile) throws GitException {
      this.gitFile = gitFile;
      this.isSelected = gitFile.isStaged();
    }

    /**
     * Returns the selection state of the item
     */
    boolean isSelected() {
      return isSelected;
    }

    /**
     * Setter for the selection state. When the user clicks on a list entry, its selection state will change
     */
    void setSelected(boolean isSelected) {
      this.isSelected = isSelected;
    }

    /**
     * Method to retrieve the GitFile instance that is encapsulated in the list item
     */
    GitFile getGitFile() {
      return gitFile;
    }


  }


}
