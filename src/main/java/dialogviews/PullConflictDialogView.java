package dialogviews;


import commands.ICommand;
import commands.ICommandGUI;
import commands.Merge;
import commands.Rebase;
import controller.GUIController;
import git.GitBranch;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PullConflictDialogView implements IDialogView {


  private JButton cancelButton;
  private JButton rebaseButton;
  private JButton mergeButton;
  private JTextArea conflictMessage;
  private JPanel pullConflictPanel;
  private GitBranch src;
  private GitBranch dest;

  public PullConflictDialogView(GitBranch src, GitBranch dest) {
    this.src = src;
    this.dest = dest;
    mergeButton.setEnabled(false);
    rebaseButton.setEnabled(false);
    conflictMessage.setRows(3);
    conflictMessage.setEnabled(false);
    conflictMessage.setDisabledTextColor(Color.black);
    conflictMessage.setWrapStyleWord(true);
    conflictMessage.setLineWrap(true);
    conflictMessage.setText("Die Änderungen auf dem Server konfligieren mit den lokalen Änderungen. " +
              "Die Änderungen müssen verschmolzen werden.");
    int width = conflictMessage.getWidth();
    if(width > 0) {
      conflictMessage.setSize(width, Short.MAX_VALUE);
    }
    List<ICommand> commandList = Settings.getInstance().getLevel().getCommands();
    boolean merge = false;
    for(int i = 0; i < commandList.size(); i++) {
      if(commandList.get(i).getName().compareTo("Merge") == 0) {
        mergeButton.setEnabled(true);
        merge = true;
      } else if(commandList.get(i).getName().compareTo("Rebase") == 0) {
        rebaseButton.setEnabled(true);
        merge = true;
      }
    }
    if(!merge) {
      mergeButton.setEnabled(true);
    }
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIController.getInstance().closeDialogView();
      }
    });
    rebaseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Rebase rebase = new Rebase(src, dest);
        rebase.execute();
      }
    });
    mergeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Merge merge = new Merge(src, dest);
        merge.execute();
      }
    });
  }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Konflikte";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
      Dimension dim = new Dimension(400, 200);
      return dim;
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return pullConflictPanel;
    }

    public void update() {

    }

    public void setErrorMessage(String errorMessage) {

    }
}