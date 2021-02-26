package views;

import controller.GUIController;
import git.*;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Creates the lower part of the {@link MainWindow}. It shows all Commits of
 * the active Branch of the active git Repository on the left side. The first Commit shown is
 * the latest one. In the middle are the committed files of the selected Commit.
 * On top of the right side is the full commit message and the author of the selected Commit.
 * Below is the difference between the selected file and the former version of the file.
 */
public class HistoryView extends JPanel implements IView {
  private final DiffView diffView;
  private JList<String> commitList;
  private JScrollPane commitScrollPane;
  private JList<String> fileList;
  private JTextArea commitMessage;
  private JScrollPane fileScrollPane;
  private JPanel historyViewPanel;
  private JScrollPane diffPane;
  private JPanel diffPanel;
  @SuppressWarnings("unused")
  private JScrollPane commitMessageScrollPane;
  private final JTextPane diffText;
  private Iterator<GitCommit> iteratorOfCommits;
  private final ArrayList<GitCommit> listOfCommits = new ArrayList<>();
  private List<GitFile> listOfFiles;
  private Iterator<GitFile> gitFileIterator;
  private DefaultListModel<String> fileListModel;
  private int maxCommits = 20;
  private int loadedCommits = 0;
  private int maxFiles;
  private int loadedFiles;
  private final DefaultListModel<String> listModel;

  /**
   * {@inheritDoc}
   */
  public JPanel getView() {
    return new HistoryView().historyViewPanel;
  }

  /**
   * {@inheritDoc}
   */
  public void update() {
    // This method is not used because it is not needed.
  }

  /**
   * Creates the content of the commit list. This is located at
   * the left side of the JPanel.
   */
  public HistoryView() {
    commitScrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    diffPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    // To set the size
    commitMessage.setText(System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());
    commitMessage.setEnabled(false);
    commitMessage.setVisible(false);
    commitMessage.setDisabledTextColor(Color.BLACK);
    listModel = new DefaultListModel<>();
    commitList.setModel(listModel);
    commitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    diffView = new DiffView();
    diffText = diffView.openDiffView();
    diffPanel.add(diffText);
    diffText.setEditable(false);
    applyCellRenderer();
    GitData data = new GitData();
    try {
      GitBranch branch = data.getSelectedBranch();
      if (data.getBranches().isEmpty()) {
        return;
      }
      iteratorOfCommits = branch.getCommits();
    } catch (GitException | IOException e) {
      GUIController.getInstance().errorHandler(e.getMessage());
    }
    addCommits();
    addScrollbarListener();
    addListSelectionListeners();
  }

  private void applyCellRenderer() {
    commitList.setCellRenderer(new HistoryViewRenderer<>(6));
    fileList.setCellRenderer(new HistoryViewRenderer<>(1));
  }

  /**
   * Adds mouseListeners to the commitList and the fileList.
   */
  private void addListSelectionListeners() {
    commitList.addListSelectionListener(e -> {
      maxFiles = 50;
      loadedFiles = 0;
      diffView.setNotVisible();
      int index = commitList.getSelectedIndex();
      if (index < 0) {
        return;
      }
      GitCommit selectedCommit = listOfCommits.get(index);
      String activeMessage = selectedCommit.getMessage();
      GitAuthor author = selectedCommit.getAuthor();
      String name = author.getName();
      String eMail = author.getEmail();
      Date date = selectedCommit.getDate();
      DateFormat format = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss");
      String commitDate = format.format(date);
      commitMessage.setText("Autor: " + name + System.lineSeparator()
              + "E-Mail: " + eMail + System.lineSeparator()
              + "Datum: " + commitDate + " Uhr" + System.lineSeparator()
              + System.lineSeparator()
              + activeMessage);
      commitMessage.setCaretPosition(0);
      fileListModel = new DefaultListModel<>();
      fileList.setModel(fileListModel);
      fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      int width = commitMessage.getWidth();
      commitMessage.setVisible(true);
      if (width > 0) {
        commitMessage.setSize(width, Short.MAX_VALUE);
      }
      commitMessage.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      commitMessage.setLineWrap(true);
      commitMessage.setWrapStyleWord(true);
      try {
        listOfFiles = selectedCommit.getChangedFiles();
      } catch (IOException ioException) {
        GUIController.getInstance().errorHandler(ioException);
        return;
      }
      gitFileIterator = listOfFiles.iterator();
      addFiles();
    });
    fileList.addListSelectionListener(e -> {
      int fileIndex = fileList.getSelectedIndex();
      int commitIndex = commitList.getSelectedIndex();
      if (fileIndex < 0 || commitIndex < 0) {
        return;
      }
      GitFile file = listOfFiles.get(fileIndex);
      GitCommit commit = listOfCommits.get(commitIndex);
      diffView.setDiff(commit, file);
      diffText.setCaretPosition(0);
    });
  }

  private void addScrollbarListener() {
    commitScrollPane.getVerticalScrollBar().addAdjustmentListener(ae -> {
      int extent = commitScrollPane.getVerticalScrollBar().getModel().getExtent();
      int max = commitScrollPane.getVerticalScrollBar().getMaximum();
      if (max == extent + commitScrollPane.getVerticalScrollBar().getModel().getValue()) {
        maxCommits += 20;
        addCommits();
      }
    });
    fileScrollPane.getVerticalScrollBar().addAdjustmentListener(ae -> {
      int extent = fileScrollPane.getVerticalScrollBar().getModel().getExtent();
      int max = fileScrollPane.getVerticalScrollBar().getMaximum();
      if (max == extent + fileScrollPane.getVerticalScrollBar().getModel().getValue()) {
        maxFiles += 50;
        if (gitFileIterator != null)
          addFiles();
      }
    });
  }

  private void addFiles() {
    while (gitFileIterator.hasNext() && loadedFiles < maxFiles) {
      String activeFile = gitFileIterator.next().getRelativePath();
      fileListModel.addElement(activeFile);
      loadedFiles++;
    }
  }

  private void addCommits() {
    while (iteratorOfCommits.hasNext() && loadedCommits < maxCommits) {
      GitCommit current = iteratorOfCommits.next();
      String message = current.getShortMessage();
      listOfCommits.add(loadedCommits, current);
      listModel.addElement(message);
      loadedCommits++;
    }
  }

  private static class HistoryViewRenderer<String> implements ListCellRenderer<String> {
    private final int minRows;
    private final JTextArea area;

    /**
     * Sets the minimal amount of rows required by one list entry. A list entry has to be a String
     * in order to work properly. If the item size should dynamically grow set minRows = 1.
     * The JList which wants to dynamically grow needs to invoke a componentListener.
     * For an example how to use the componentListener look in the class HistoryView.
     *
     * @param minRows the minimal count of rows contained in one ListCell.
     */
    public HistoryViewRenderer(int minRows) {
      this.minRows = minRows;
      area = new JTextArea();
      area.setLineWrap(true);
      area.setWrapStyleWord(true);
    }

    @Override
    public Component getListCellRendererComponent(final JList list,
                                                  final Object value, final int index, final boolean isSelected,
                                                  final boolean hasFocus) {
      Color background = Color.WHITE;
      area.setText((java.lang.String) value);
      // Only the first 6 lines of the commit message should be shown.
      area.setRows(minRows);
      int width = list.getWidth();
      if (isSelected) {
        // This color is light blue.
        background = new Color(0xAAD8E6);
      }
      area.setBackground(background);
      // this is just to activate the JTextAreas internal sizing mechanism
      if (width > 0) {
        area.setSize(width, Short.MAX_VALUE);
      }
      area.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      return area;

    }
  }
}
