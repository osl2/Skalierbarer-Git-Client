package views.addCommitView;

import commands.Add;
import commands.Commit;
import controller.GUIController;
import git.GitData;
import git.GitFile;
import git.GitStatus;
import git.exception.GitException;
import settings.Settings;
import views.IView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
  private JList<FileListItem> addedFilesList;
  private JPanel diffPanel;
  private JScrollPane addedFilesScrollPane;
  private JTextField testTextField;
  private FileListRenderer renderer;
  private GUIController c;
  private Add addCommand;
  private Commit commitCommand;
  private GitStatus gitStatus;
  private GitData gitData;
  private Settings settings;

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

    setUpFileList();

  }

  public JPanel getView() {
    return addCommitView;
  }


  public void update() {
    //TODO: wird die überhaupt benötigt?
  }

  private void createUIComponents() {
    renderer = new FileListRenderer();
    addedFilesList = new JList<FileListItem>();
    addedFilesList.setCellRenderer(renderer);
    //TODO: diff area
  }

  private boolean executeAdd(){
    boolean success = false;

    //pass all selected FileListItems to add
    List<GitFile> filesToBeAdded = new LinkedList<>();

    for (FileListItem item : addedFilesList.getSelectedValuesList()){
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
  private void setUpFileList(){

    DefaultListModel<FileListItem> defaultListModel = new DefaultListModel();

    List<GitFile> uncommittedChanges;
    List<FileListItem> fileListItems = new LinkedList<>();
    try {
      uncommittedChanges = gitStatus.getUncommittedChanges();
    }
    catch (GitException | IOException e){
      //in case of an exeption, create empty list
      //TODO: rework in case GitFile and GitStatus continue throwing GitExceptions or IOExeptions
      uncommittedChanges = new LinkedList<>();
      c.errorHandler(e);
    }
    for (GitFile file : uncommittedChanges){
      FileListItem item = new FileListItem(file);
      fileListItems.add(item);
    }

    defaultListModel.addAll(fileListItems);
    addedFilesList.setModel(defaultListModel);


    //Mouse Listener for Checkboxes. Selected files are being marked for git add
    addedFilesList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        JList list = (JList) event.getSource();
        //get the item index that was clicked
        int index = list.locationToIndex(event.getPoint());
        FileListItem item = (FileListItem) list.getModel().getElementAt(index);
        //if item was not selected before, select; otherwise, deselect
        item.setSelected(!item.isSelected());
        //repaint cell
        list.repaint(list.getCellBounds(index, index));

      }
    });

    //List selection listener for a single item. Call git diff on the selected item
    addedFilesList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        JList<FileListItem> list = (JList<FileListItem>) e.getSource();
        for (FileListItem item : list.getSelectedValuesList()) {
          //TODO: call git diff
        }
      }
    });




  }
}
