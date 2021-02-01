package views;

import git.GitCommit;
import views.filter.AbstractHistoryFilter;

import git.GitCommit;
import views.filter.AbstractHistoryFilter;

import javax.swing.*;

public class HistoryView extends JPanel implements IView {
    private JList list1;
    private JScrollPane scrollPane;
    private JList list2;
    public JPanel getView() {
        return this;
    }

    public void update() {

    }

    private void applyFilter(AbstractHistoryFilter filter) {
    }

    public void getFiles(GitCommit commit) {}
}
