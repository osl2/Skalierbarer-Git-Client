package views;

import commands.Log;
import git.GitCommit;
import views.filter.AbstractHistoryFilter;

import git.GitCommit;
import views.filter.AbstractHistoryFilter;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class HistoryView extends JPanel implements IView {
    private IDiffView diffView;
    private JList commitList;
    private JScrollPane commitScrollPane;
    private JList fileList;
    private JLabel commitMessage;
    private JScrollPane fileScrollPane;
    private JPanel historyView;
    private Log log;
    private List<GitCommit> listOfCommits;
    private List<String> dummyListe1 = new ArrayList<String>();
    private List<String> dummyListe2 = new ArrayList<String>();

    private void initList() {
        for(int i = 0; i < 10; i++) {
            dummyListe1.add("Eintraggggggg" + System.lineSeparator() +
                    "ggggggggggggggggggggggg \n" +
                    "ggggggggggg ggggggggggggggggg gggggggggggg gggggggggggggggg ggggggggggggg ggggggggg gggggggggggggggg ggggggggggggggg ggggggggg gggggggg" +
                    "ggggggg gggggggggggggggg ggggggggggggggggg gggggggggggg gggggggggggggg gggggggggggggggggg gggggggggggggg ggggggggggggggggg ggggggg" + i);
            dummyListe2.add("Eintrag" + i);
            System.out.println(dummyListe1.get(i));
        }
    }

    /**
     * Creates the content of the commit list. This is located at
     * the left side of the JPanel.
     */
    public HistoryView() {
        //this.add(new HistoryView().historyView);
        DefaultListModel listModel = new DefaultListModel();
        commitList.setModel(listModel);
        commitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        commitList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int index = commitList.getSelectedIndex();
                //GitCommit selectedCommit = listOfCommits.get(index);
                DefaultListModel fileListModel = new DefaultListModel();
                fileList.setModel(fileListModel);
                fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                //
                String s = dummyListe1.get(index);
                for(int i = 0; i < 5; i++) {
                    fileListModel.addElement(dummyListe2.get(i));
                }
            }
        });
    }

    public JPanel getView() {
        return this;
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
        commitList.setCellRenderer(new views.listCellRenderer.HistoryViewRenderer());
    }
}
