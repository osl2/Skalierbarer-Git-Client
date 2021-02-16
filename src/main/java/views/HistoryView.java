package views;

import commands.Log;
import git.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import settings.Settings;
import views.filter.AbstractHistoryFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryView extends JPanel implements IView {
  private IDiffView diffView;
  private JList commitList;
  private JScrollPane commitScrollPane;
  private JList fileList;
  private JTextArea commitMessage;
  private JScrollPane fileScrollPane;
  private JPanel historyView;
  private JScrollPane diffPane;
  private Log log;
  private List<GitCommit> listOfCommits;
  private List<GitFile> listOfFiles;
  private List<String> dummyListe1 = new ArrayList<String>();
  private List<String> dummyListe2 = new ArrayList<String>();

  private File path = new File("D:\\Eclipse_Workplace_5\\.git");
  private File file = new File("D:\\Eclipse_Workplace_5");
  GitData gitData;
  Git git;
  Repository repository;

  private void initList() {
    for(int i = 0; i < 10; i++) {
      dummyListe1.add("Eintraggggggg" + System.lineSeparator() +
              "ggggggggggggggggggggggg \n" +
              "Guten Tag am heutigen Tag haben wiraber ein sehr schönes Wetter nicht wahr. Es scheint das die Sonne scheint. ggggggggggg ggggggggggggggggg gggggggggggg gggggggggggggggg ggggggggggggg ggggggggg gggggggggggggggg ggggggggggggggg ggggggggg gggggggg" +
              "ggggggg gggggggggggggggg ggggggggggggggggg gggggggggggg gggggggggggggg gggggggggggggggggg gggggggggggggg ggggggggggggggggg ggggggg" + i);
      dummyListe2.add("Eintraggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" + i);
      System.out.println(dummyListe1.get(i));
    }
    dummyListe1.add(0, "Hallo");
  }

  private void initRepo() {
    /*FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
    repositoryBuilder.setMustExist( true );
    repositoryBuilder.setGitDir(path);
    try {
      repository = repositoryBuilder.build();
    } catch (IOException e) {
      e.printStackTrace();
    }*/
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
    System.out.println(branchName);
  }

  /**
   * Creates the content of the commit list. This is located at
   * the left side of the JPanel.
   */
  public HistoryView() {
    initRepo();
    commitScrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    fileScrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    commitMessage.setEnabled(false);
    commitMessage.setVisible(false);
    commitMessage.setDisabledTextColor(Color.BLACK);
    DefaultListModel listModel = new DefaultListModel();
    commitList.setModel(listModel);
    commitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    diffView = new DiffView();
    diffPane.add(diffView.openDiffView());
    applyCellRenderer();
    log = new Log();
    log.execute();
    listOfCommits = log.getCommits(null);
    int entries = listOfCommits.size();

    //
    //entries = 11;
    //initList();
    //
    for(int i = 0; i < entries; i++) {
      GitCommit current = listOfCommits.get(i);
      String message = current.getMessage();
      listModel.addElement(message);
      //listModel.addElement(dummyListe1.get(i));
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
        int index = commitList.getSelectedIndex();
        GitCommit selectedCommit = listOfCommits.get(index);
        String activeMessage = selectedCommit.getMessage();
        GitAuthor author = selectedCommit.getAuthor();
        String name = author.getName();
        String eMail = author.getEmail();
        Date date = selectedCommit.getDate();
        commitMessage.setText("Autor: " + name + ", E-Mail: " + eMail + ", Datum: " + date + ": " + activeMessage);
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
        /*for(int i = 0; i < 5; i++) {
          fileListModel.addElement(dummyListe2.get(i));
        }*/
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
