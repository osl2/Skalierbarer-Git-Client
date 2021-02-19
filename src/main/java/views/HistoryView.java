package views;

import controller.GUIController;
import git.*;
import git.exception.GitException;
import views.filter.AbstractHistoryFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
  private JTextPane diffText;
  private GitData data;
  private Iterator<GitCommit> iteratorOfCommits;
  private ArrayList<GitCommit> listOfCommits = new ArrayList<>();
  private List<GitFile> listOfFiles;
  private Iterator<GitFile> gitFileIterator;
  private DefaultListModel fileListModel;
  private GitBranch branch;
  private int maxCommits = 20;
  private int loadedCommits = 0;
  private int maxFiles;
  private int loadedFiles;
  private DefaultListModel listModel;


  public JPanel getView() {
    return new HistoryView().historyView;
  }

  public void update() {

  }

  private void applyFilter(AbstractHistoryFilter filter) {
  }

  /**
   * Creates the content of the commit list. This is located at
   * the left side of the JPanel.
   */
  public HistoryView() {
    commitScrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    commitMessage.setEnabled(false);
    commitMessage.setVisible(false);
    commitMessage.setDisabledTextColor(Color.BLACK);
    listModel = new DefaultListModel();
    commitList.setModel(listModel);
    commitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    diffView = new DiffView();
    diffText = diffView.openDiffView();
    diffPanel.add(diffText);
    diffText.setEditable(false);
    applyCellRenderer();
    data = new GitData();
    try {
      branch = data.getSelectedBranch();
      if (data.getBranches().size() == 0) {
        return;
      }
      iteratorOfCommits = branch.getCommits();
    } catch (GitException e) {
      GUIController.getInstance().errorHandler(e.getMessage());
    } catch (IOException e) {
      GUIController.getInstance().errorHandler(e.getMessage());
    }
    addCommits();
    addScrollbarListener();
    addMouseListeners();
  }

  private void applyCellRenderer() {
    commitList.setCellRenderer(new HistoryViewRenderer(6));
    fileList.setCellRenderer(new HistoryViewRenderer(1));
  }

  public void getFiles(GitCommit commit) {
  }

  /**
   * Adds mouseListeners to the commitList and the fileList.
   */
  private void addMouseListeners() {
    commitList.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
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
        fileListModel = new DefaultListModel();
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
      }
    });
    fileList.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        int fileIndex = fileList.getSelectedIndex();
        int commitIndex = commitList.getSelectedIndex();
        if (fileIndex < 0 || commitIndex < 0) {
          return;
        }
        GitFile file = listOfFiles.get(fileIndex);
        GitCommit commit = listOfCommits.get(commitIndex);
        diffView.setDiff(commit, file);
        diffText.setCaretPosition(0);
      }
    });
  }

  private void addScrollbarListener() {
    commitScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent ae) {
        int extent = commitScrollPane.getVerticalScrollBar().getModel().getExtent();
        int max = commitScrollPane.getVerticalScrollBar().getMaximum();
        if (max == extent + commitScrollPane.getVerticalScrollBar().getModel().getValue()) {
          maxCommits += 20;
          addCommits();
        }
      }
    });
    fileScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent ae) {
        int extent = fileScrollPane.getVerticalScrollBar().getModel().getExtent();
        int max = fileScrollPane.getVerticalScrollBar().getMaximum();
        if (max == extent + fileScrollPane.getVerticalScrollBar().getModel().getValue()) {
          maxFiles += 50;
          if (gitFileIterator != null)
            addFiles();
        }
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
    historyView = new JPanel();
    historyView.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    historyView.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 2, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    commitScrollPane = new JScrollPane();
    commitScrollPane.setEnabled(true);
    commitScrollPane.setHorizontalScrollBarPolicy(31);
    panel1.add(commitScrollPane, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(100, -1), new Dimension(300, -1), new Dimension(301, -1), 0, false));
    commitList = new JList();
    final DefaultListModel defaultListModel1 = new DefaultListModel();
    commitList.setModel(defaultListModel1);
    commitScrollPane.setViewportView(commitList);
    fileScrollPane = new JScrollPane();
    fileScrollPane.setHorizontalScrollBarPolicy(31);
    panel1.add(fileScrollPane, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(100, -1), new Dimension(300, -1), new Dimension(301, -1), 0, false));
    fileList = new JList();
    final DefaultListModel defaultListModel2 = new DefaultListModel();
    fileList.setModel(defaultListModel2);
    fileScrollPane.setViewportView(fileList);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    historyView.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    commitMessage = new JTextArea();
    commitMessage.setText("");
    panel2.add(commitMessage, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, -1), new Dimension(600, -1), new Dimension(680, -1), 0, false));
    diffPane = new JScrollPane();
    diffPane.setHorizontalScrollBarPolicy(30);
    historyView.add(diffPane, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    diffPanel = new JPanel();
    diffPanel.setLayout(new CardLayout(0, 0));
    diffPane.setViewportView(diffPanel);
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return historyView;
  }

  private static class HistoryViewRenderer extends JTextArea implements ListCellRenderer {
    private int minRows;

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
      if (isSelected) {
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
