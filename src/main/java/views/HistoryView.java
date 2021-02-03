package views;

import commands.Log;
import git.GitCommit;
import git.GitFile;
import views.filter.AbstractHistoryFilter;

import git.GitCommit;
import views.filter.AbstractHistoryFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

  /**
   * Creates the content of the commit list. This is located at
   * the left side of the JPanel.
   */
  public HistoryView() {
    //this.add(new HistoryView().historyView);
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
    //log = new Log();
    //log.execute();
    // Branch name fehlt noch
    int entries; //= listOfCommits.size();

    //
    entries = 10;
    initList();
    //
    for(int i = 0; i < entries; i++) {
      //GitCommit current = listOfCommits.get(i);
      //String message = current.getMessage();
      //listModel.addElement(message);
      listModel.addElement(dummyListe1.get(i));
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

  public static void main(String arg[]) {
    JFrame frame = new JFrame("HistoryView");
    frame.setContentPane(new HistoryView().historyView);
    JPanel panel = new JPanel();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setSize(1280, 720);
    frame.setVisible(true);
  }

  private void applyCellRenderer() {
    commitList.setCellRenderer(new views.listCellRenderer.HistoryViewRenderer(6));
    fileList.setCellRenderer(new views.listCellRenderer.HistoryViewRenderer(1));
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
        //GitCommit selectedCommit = listOfCommits.get(index);
        //String activeMessage = selectedCommit.getMessage();
        //GitAuthor author = selectedCommit.getAuthor();
        //String name = author.getName();
        //String eMail = author.getEmail();
        //Date date = selectedCommit.getDate();
        //commitMessage.setText(name + " " + eMail + " " + date ": " + activeMessage);
        DefaultListModel fileListModel = new DefaultListModel();
        fileList.setModel(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //
        String s = dummyListe1.get(index);
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
        commitMessage.setText(s);
        for(int i = 0; i < 5; i++) {
          fileListModel.addElement(dummyListe2.get(i));
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
      }
    });
  }
}
