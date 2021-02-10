package views;

import commands.Log;
import git.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import settings.Settings;
import views.filter.AbstractHistoryFilter;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryView extends JPanel implements IView {
  private DiffView diffView;
  private JList commitList;
  private JScrollPane commitScrollPane;
  private JList fileList;
  private JTextArea commitMessage;
  private JScrollPane fileScrollPane;
  private JPanel historyView;
  private JScrollPane diffPane;
  private JPanel diffPanel;
  private JTextArea diffText;
  private Log log;
  private List<GitCommit> listOfCommits;
  private List<GitFile> listOfFiles;

  //-------------------------------Lokaler test-----------------------------------//
  private File path = new File("D:\\Eclipse_Workplace_5\\.git");
  private File file = new File("D:\\Eclipse_Workplace_5");
  GitData gitData;
  Git git;
  Repository repository;

  private void initRepo() {
    try {
      git = Git.open(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Settings.getInstance().setActiveRepositoryPath(file);
    gitData = new GitData();
    gitData.reinitialize();
    String branchName = "";
    try {
      branchName = git.getRepository().getFullBranch();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
//---------------------------------------------------------------------//

  /**
   * Creates the content of the commit list. This is located at
   * the left side of the JPanel.
   */
  public HistoryView() {
    initRepo();
    commitScrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    commitMessage.setEnabled(false);
    commitMessage.setVisible(false);
    commitMessage.setDisabledTextColor(Color.BLACK);
    DefaultListModel listModel = new DefaultListModel();
    commitList.setModel(listModel);
    commitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    diffView = new DiffView();
    diffText = diffView.openDiffView();
    diffPanel.add(diffText);
    applyCellRenderer();
    log = new Log();
    log.execute();
    listOfCommits = log.getCommits(null);
    int entries = listOfCommits.size();
    for(int i = 0; i < entries; i++) {
      GitCommit current = listOfCommits.get(i);
      String message = current.getMessage();
      listModel.addElement(message);
    }
    addMouseListeners();
  }

  public JPanel getView() {
    return new HistoryView().historyView;
  }

  public void update() {

  }

  private void applyFilter(AbstractHistoryFilter filter) {
  }

  public void getFiles(GitCommit commit) {}

  private void applyCellRenderer() {
    commitList.setCellRenderer(new HistoryViewRenderer(6));
    fileList.setCellRenderer(new HistoryViewRenderer(1));
  }

  /**
   * Adds mouseListeners to the commitList and the fileList.
   */
  private void addMouseListeners() {
    commitList.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        diffView.setNotVisible();
        int index = commitList.getSelectedIndex();
        GitCommit selectedCommit = listOfCommits.get(index);
        String activeMessage = selectedCommit.getMessage();
        GitAuthor author = selectedCommit.getAuthor();
        String name = author.getName();
        String eMail = author.getEmail();
        Date date = selectedCommit.getDate();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss");
        String commitDate = format.format(date);
        commitMessage.setText("Autor: " + name + ", E-Mail: " + eMail + ", Datum: " + commitDate + " Uhr: " + activeMessage);
        DefaultListModel fileListModel = new DefaultListModel();
        fileList.setModel(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //
        int width = commitMessage.getWidth();
        commitMessage.setVisible(true);
        // Margin not working
        Insets inset = new Insets(10, 10, 10, 10);
        commitMessage.setMargin(inset);
        if(width > 0) {
          commitMessage.setSize(width, Short.MAX_VALUE);
        }
        commitMessage.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        commitMessage.setLineWrap(true);
        commitMessage.setWrapStyleWord(true);
        try {
          listOfFiles = selectedCommit.getChangedFiles();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
        int size = listOfFiles.size();
        for(int i = 0; i < size; i++) {
          String activeFile = listOfFiles.get(i).getPath().getName();
          fileListModel.addElement(activeFile);
        }
      }
    });
    fileList.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        int fileIndex = fileList.getSelectedIndex();
        int commitIndex = commitList.getSelectedIndex();
        GitFile file = listOfFiles.get(fileIndex);
        GitCommit commit = listOfCommits.get(commitIndex);
        diffView.setDiff(commit, file);
        diffText.setCaretPosition(0);
      }
    });
  }

  private static class HistoryViewRenderer extends JTextArea implements ListCellRenderer {
    private int minRows;

    /**
     * Sets the minimal amount of rows required by one list entry. A list entry has to be a String
     * in order to work properly. If the item size should dynamically grow set minRows = 1.
     * The JList which wants to dynamically grow needs to invoke a componentListener.
     * For an example how to use the componentListener look in the class HistoryView.
     * @param minRows the minimal count of rows contained in one ListCell.
     */
    public HistoryViewRenderer(int minRows) {
      this.minRows = minRows;
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
      this.setRows(minRows);
      int width = list.getWidth();
      if(isSelected) {
        // This color is light blue.
        background = new Color(0xAAD8E6);
      }
      this.setBackground(background);
      // this is just to activate the JTextAreas internal sizing mechanism
      if (width > 0) {
        this.setSize(width, Short.MAX_VALUE);
      }
      this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      return this;

    }
  }
}
