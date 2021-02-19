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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

public class PullConflictDialogView implements IDialogView {


  private JButton cancelButton;
  private JButton rebaseButton;
  private JButton mergeButton;
  private JTextArea conflictMessage;
  private JPanel pullConflictPanel;
  private GitBranch src;
  private GitBranch dest;
  private String commandLine;

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

  public PullConflictDialogView(GitBranch src, GitBranch dest, String commandLine) {
    this.src = src;
    this.dest = dest;
    this.commandLine = commandLine;
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
    for (int i = 0; i < commandList.size(); i++) {
      if (Level.iCommandGUIEquals(Collections.singletonList(commandList.get(i)), mergeCommand)) {
        mergeButton.setEnabled(true);
        merge = true;
      } else if (Level.iCommandGUIEquals(Collections.singletonList(commandList.get(i)), rebaseCommand)) {
        rebaseButton.setEnabled(true);
        merge = true;
      }
    }
    if (!merge) {
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
        GUIController.getInstance().closeDialogView();
        Rebase rebase = new Rebase(src, dest);
        boolean success = rebase.execute();
        if (success) {
          GUIController.getInstance().setCommandLine("git pull --rebase " + commandLine);
        }
      }
    });
    mergeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIController.getInstance().closeDialogView();
        Merge merge = new Merge(src, dest);
        boolean success = merge.execute();
        if (success) {
          GUIController.getInstance().setCommandLine("git pull " + commandLine);
        }
      }
    });
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
    pullConflictPanel = new JPanel();
    pullConflictPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 5, new Insets(0, 0, 0, 0), -1, -1));
    cancelButton = new JButton();
    cancelButton.setText("Abbruch");
    pullConflictPanel.add(cancelButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
    pullConflictPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    rebaseButton = new JButton();
    rebaseButton.setText("Rebase");
    pullConflictPanel.add(rebaseButton, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    mergeButton = new JButton();
    mergeButton.setText("Merge");
    pullConflictPanel.add(mergeButton, new com.intellij.uiDesigner.core.GridConstraints(2, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
    pullConflictPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
    pullConflictPanel.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(2, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
    pullConflictPanel.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    conflictMessage = new JTextArea();
    conflictMessage.setText("");
    pullConflictPanel.add(conflictMessage, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(240, -1), new Dimension(280, -1), new Dimension(300, -1), 0, false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return pullConflictPanel;
  }

}