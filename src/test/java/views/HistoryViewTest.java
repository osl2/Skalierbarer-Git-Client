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

import commands.AbstractCommandTest;
import dialogviews.FindComponents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;


class HistoryViewTest extends AbstractCommandTest {
  JList commitList;
  JList fileList;
  JTextPane diffText;
  JTextArea commitMessage;
  private HistoryView history;

  @BeforeEach
  void findGuiComponents() {
    history = new HistoryView();
    JPanel historyPanel = history.getView();
    commitList = (JList) FindComponents.getChildByName(historyPanel, "commitList");
    assertNotNull(commitList);
    fileList = (JList) FindComponents.getChildByName(historyPanel, "fileList");
    assertNotNull(fileList);
    diffText = (JTextPane) FindComponents.getChildByName(historyPanel, "diffText");
    assertNotNull(diffText);
    commitMessage = (JTextArea) FindComponents.getChildByName(historyPanel, "commitMessage");
    assertNotNull(commitMessage);
  }

  @Test
  void loadHistoryView() {
    commitList.setSelectedIndex(2);
    fileList.setSelectedIndex(0);
    assertNotEquals("", commitMessage.getText());
    assertNotEquals("", diffText.getText());
  }

  @Test
  void globalTestCaseDiff_T7() {
    commitList.setSelectedIndex(2);
    fileList.setSelectedIndex(0);
    System.out.println(diffText.getText());
    assertTrue(diffText.getText().contains("-data 1"));
    assertTrue(diffText.getText().contains("+data 1data 2"));
  }
}
