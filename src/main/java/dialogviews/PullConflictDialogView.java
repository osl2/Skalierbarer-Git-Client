package dialogviews;


import commands.ICommandGUI;
import commands.Merge;
import commands.Pull;
import commands.Rebase;
import controller.GUIController;
import git.GitBranch;
import levels.Level;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a window to choose whether the fetched branch should
 * be merged or rebased.
 */
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

  @Override
  public void update() {
    // This method is not used because it is not needed.
  }

  /**
   * Creates a new PullConflictDialogView to handle pull conflicts.
   *
   * @param src         the fetched branch from the Remote.
   * @param dest        the locale branch to merge or rebase into.
   * @param commandLine the commandLine output of the {@link Pull} command.
   */
  public PullConflictDialogView(GitBranch src, GitBranch dest, String commandLine) {
    setNameComponents();
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
    rebaseButton.setEnabled(false);
    cancelButton.addActionListener(e -> GUIController.getInstance().closeDialogView());
    mergeButton.addActionListener(e -> {
      GUIController.getInstance().closeDialogView();
      Merge merge1 = new Merge(src, dest);
      boolean success = merge1.execute();
      if (success) {
        GUIController.getInstance().setCommandLine("git pull " + commandLine);
      }
    });
  }

  /**
   * This method is needed in order to execute the GUI tests successfully.
   * Do not change otherwise tests might fail.
   */
  private void setNameComponents() {
    mergeButton.setName("mergeButton");
    cancelButton.setName("cancelButton");
  }

}