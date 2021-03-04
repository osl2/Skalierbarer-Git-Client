package views;

import commands.AbstractCommandTest;
import dialogviews.FindComponents;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;


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
    assertEquals("", diffText.getText());
  }
}
