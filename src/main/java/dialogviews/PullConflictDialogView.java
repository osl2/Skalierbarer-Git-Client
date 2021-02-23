package dialogviews;


import commands.ICommandGUI;
import commands.Merge;
import commands.Rebase;
import controller.GUIController;
import git.GitBranch;
import levels.Level;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class PullConflictDialogView implements IDialogView {


  private JButton cancelButton;
  private JButton rebaseButton;
  private JButton mergeButton;
  private JTextArea conflictMessage;
  private JPanel pullConflictPanel;

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
    return new Dimension(400, 200);
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

  public PullConflictDialogView(GitBranch src, GitBranch dest, String commandLine) {
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
    if (width > 0) {
      conflictMessage.setSize(width, Short.MAX_VALUE);
    }
    boolean merge = false;
    List<ICommandGUI> mergeCommand = Collections.singletonList(new Merge());
    List<ICommandGUI> rebaseCommand = Collections.singletonList(new Rebase());
    List<ICommandGUI> commandList = Settings.getInstance().getLevel().getCommands();
    for (ICommandGUI iCommandGUI : commandList) {
      if (Level.iCommandGUIEquals(Collections.singletonList(iCommandGUI), mergeCommand)) {
        mergeButton.setEnabled(true);
        merge = true;
      } else if (Level.iCommandGUIEquals(Collections.singletonList(iCommandGUI), rebaseCommand)) {
        rebaseButton.setEnabled(true);
        merge = true;
      }
    }
    if (!merge) {
      mergeButton.setEnabled(true);
    }
    cancelButton.addActionListener(e -> GUIController.getInstance().closeDialogView());
    rebaseButton.addActionListener(e -> {
      GUIController.getInstance().closeDialogView();
      Rebase rebase = new Rebase(src, dest);
      boolean success = rebase.execute();
      if (success) {
        GUIController.getInstance().setCommandLine("git pull --rebase " + commandLine);
      }
    });
    mergeButton.addActionListener(e -> {
      GUIController.getInstance().closeDialogView();
      Merge merge1 = new Merge(src, dest);
      boolean success = merge1.execute();
      if (success) {
        GUIController.getInstance().setCommandLine("git pull " + commandLine);
      }
    });
  }

}