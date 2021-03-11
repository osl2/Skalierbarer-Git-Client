/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package views;

import commands.Diff;
import git.GitCommit;
import git.GitFile;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.logging.Logger;

/**
 * This class is for creating the visual output of git diff commands.
 */
public class DiffView implements IDiffView {
  private final Diff diff = new Diff();
  private final JTextPane pane = new JTextPane();
  private final SimpleAttributeSet addLine = new SimpleAttributeSet();
  private final SimpleAttributeSet removeLine = new SimpleAttributeSet();
  private final SimpleAttributeSet normalLine = new SimpleAttributeSet();


  /**
   * Create a new View
   */
  public DiffView() {
    StyleConstants.setForeground(addLine, new Color(0, 150, 0) /* Dark Green */);
    StyleConstants.setForeground(removeLine, Color.RED);
    StyleConstants.setForeground(normalLine, Color.BLACK);
  }


  @Override
  public JTextPane openDiffView() {
    return pane;
  }

  /**
   * Opens the difference between the given commit and the previous one.
   * Only the git diff of the given File will be displayed.
   *
   * @param activeCommit is the commit to compare to its previous version.
   * @param file is a the File of the given commit.
   */
  @Override
  public void setDiff(GitCommit activeCommit, GitFile file) {
    diff.setDiffCommit(activeCommit, file);
    if (!diff.execute()) return;
    writeDiff();
  }

  @Override
  public void setDiff(GitFile file) {
    diff.setDiffFile(file);
    if (!diff.execute()) return;
    writeDiff();
  }

  /**
   * Remove the text displayed.
   */
  public void setNotVisible() {
    pane.setText("");
  }

  /**
   * Writes the output of diff.diffGit() in the JTextPane. Lines leading with + will be displayed
   * green and lines leading with - will be displayed red.
   */
  private void writeDiff() {
    String[] output = diff.diffGit();
    pane.setText("");
    if (output == null) return;
    for (String s : output) {
      Document doc = pane.getStyledDocument();
      if (s.substring(0, 1).compareTo("+") == 0) {
        try {
          doc.insertString(doc.getLength(), s + System.lineSeparator(), addLine);
        } catch (BadLocationException e) {
          Logger.getGlobal().warning(e.getMessage());
        }
      } else if (s.substring(0, 1).compareTo("-") == 0) {
        try {
          doc.insertString(doc.getLength(), s + System.lineSeparator(), removeLine);
        } catch (BadLocationException e) {
          Logger.getGlobal().warning(e.getMessage());
        }
      } else {
        try {
          doc.insertString(doc.getLength(), s + System.lineSeparator(), normalLine);
        } catch (BadLocationException e) {
          Logger.getGlobal().warning(e.getMessage());
        }
      }
    }
  }
}
