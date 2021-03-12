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
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HistoryViewTest extends AbstractCommandTest {

  @Test
  void loadHistoryView() {
    FindComponents find = new FindComponents();
    HistoryView history = new HistoryView();
    JPanel historyPanel = history.getView();
    JList commitList = (JList) find.getChildByName(historyPanel, "commitList");
    assertNotNull(commitList);
    JList fileList = (JList) find.getChildByName(historyPanel, "fileList");
    assertNotNull(fileList);
    JTextPane diffText = (JTextPane) find.getChildByName(historyPanel, "diffText");
    assertNotNull(diffText);
    JTextArea commitMessage = (JTextArea) find.getChildByName(historyPanel, "commitMessage");
    assertNotNull(commitMessage);
    commitList.setSelectedIndex(2);
    fileList.setSelectedIndex(0);
    assertNotEquals("", commitMessage.getText());
    assertNotEquals("", diffText.getText());
  }
}
