package dialogviews;

import commands.AbstractCommandTest;
import git.GitFile;
import git.GitFileConflict;
import git.conflict.AbstractHunk;
import git.conflict.ConflictHunk;
import git.conflict.TextHunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MergeConflictDialogViewTest extends AbstractCommandTest {
    GitFileConflict conflict;
    MergeConflictDialogView mcdv;
    private JPanel contentPane;
    private JTextPane leftTextPane;
    private JTextPane centerTextArea;
    private JTextPane rightTextPane;
    private JLabel leftLabel;
    private JLabel centerLabel;
    private JLabel rightLabel;
    private JScrollPane leftScrollbar;
    private JButton buttonLeftAccept;
    private JButton buttonLeftDecline;
    private JButton buttonRightAccept;
    private JButton buttonRightDecline;
    private JButton okButton;
    private JScrollPane centerScrollbar;
    private JScrollPane rightScrollbar;
    private JPanel comparisonPane;

    @BeforeEach
    void setUp() {
        conflict = mock(GitFileConflict.class);
        ArrayList<AbstractHunk> hunkList = new ArrayList<>();
        ConflictHunk chunk2 = new ConflictHunk(new String[]{"123"}, new String[]{"345", "678"});
        TextHunk chunk1 = new TextHunk(new String[]{"000"});
        hunkList.add(chunk1);
        hunkList.add(chunk2);
        // Needed for title generation.
        GitFile mockGF = mock(GitFile.class);
        File mockFile = mock(File.class);
        when(mockFile.getPath()).thenReturn("");
        when(mockGF.getPath()).thenReturn(mockFile);
        when(conflict.getGitFile()).thenReturn(mockGF);

        when(conflict.getHunkList()).thenReturn(hunkList);
        when(conflict.getConflictHunkList()).thenReturn(Collections.singletonList(chunk2));
        mcdv = new MergeConflictDialogView(conflict, "OurSide", "TheirSide");

        obtainComponents();
    }

    @Test
    @DisplayName("Merge Button stays disabled until all conflicts are resolved")
    void buttonDisabledUntilResolution() {
        assertFalse(okButton.isEnabled());

        // Resolve Conflict
        clickButton(buttonLeftAccept);
        clickButton(buttonRightDecline);

        assertTrue(okButton.isEnabled());

    }

    @Test
    @DisplayName("Accept Only Right works")
    void acceptRightTest() {
        ConflictHunk c = conflict.getConflictHunkList().get(0);
        assertEquals(0, c.getLines().length);

        clickButton(buttonRightAccept);
        assertTrue(c.isResolved());
        assertArrayEquals(c.getTheirs(), c.getLines());
    }

    @Test
    @DisplayName("Accept Only Left works")
    void acceptLeftTest() {
        ConflictHunk c = conflict.getConflictHunkList().get(0);
        assertEquals(0, c.getLines().length);

        clickButton(buttonLeftAccept);
        assertTrue(c.isResolved());
        assertArrayEquals(c.getOurs(), c.getLines());
    }

    @Test
    @DisplayName("Accept order matters 1")
    void acceptOrderTest() {
        ArrayList<String> goal = new ArrayList<>();
        ConflictHunk c = conflict.getConflictHunkList().get(0);
        Collections.addAll(goal, c.getOurs());
        Collections.addAll(goal, c.getTheirs());
        assertEquals(0, c.getLines().length);

        clickButton(buttonLeftAccept);
        clickButton(buttonRightAccept);
        assertTrue(c.isResolved());
        assertArrayEquals(goal.toArray(), c.getLines());
    }

    @Test
    @DisplayName("Accept order matters 2")
    void acceptReverseOrderTest() {
        ArrayList<String> goal = new ArrayList<>();
        ConflictHunk c = conflict.getConflictHunkList().get(0);
        Collections.addAll(goal, c.getTheirs());
        Collections.addAll(goal, c.getOurs());
        assertEquals(0, c.getLines().length);

        clickButton(buttonRightAccept);
        clickButton(buttonLeftAccept);
        assertTrue(c.isResolved());
        assertArrayEquals(goal.toArray(), c.getLines());
    }


    @Test
    @DisplayName("Accepting None is ok")
    void acceptNoneTest() {
        assertFalse(conflict.getConflictHunkList().stream().anyMatch(AbstractHunk::isResolved));
        clickButton(buttonLeftDecline);
        clickButton(buttonRightDecline);
        assertTrue(okButton.isEnabled());
        assertTrue(conflict.getConflictHunkList().stream().allMatch(AbstractHunk::isResolved));
    }

    @Test
    @DisplayName("MetaData is set")
    void metaDataTest() {
        assertNotNull(mcdv.getDimension());
        assertNotNull(mcdv.getTitle());
    }


    private void obtainComponents() {
        JPanel panel = mcdv.getPanel();
        contentPane = (JPanel) FindComponents.getChildByName(panel, "contentPane");
        leftTextPane = (JTextPane) FindComponents.getChildByName(panel, "leftTextPane");
        centerTextArea = (JTextPane) FindComponents.getChildByName(panel, "centerTextArea");
        rightTextPane = (JTextPane) FindComponents.getChildByName(panel, "rightTextPane");
        leftLabel = (JLabel) FindComponents.getChildByName(panel, "leftLabel");
        centerLabel = (JLabel) FindComponents.getChildByName(panel, "centerLabel");
        rightLabel = (JLabel) FindComponents.getChildByName(panel, "rightLabel");
        leftScrollbar = (JScrollPane) FindComponents.getChildByName(panel, "leftScrollbar");
        buttonLeftAccept = (JButton) FindComponents.getChildByName(panel, "buttonLeftAccept");
        buttonLeftDecline = (JButton) FindComponents.getChildByName(panel, "buttonLeftDecline");
        buttonRightAccept = (JButton) FindComponents.getChildByName(panel, "buttonRightAccept");
        buttonRightDecline = (JButton) FindComponents.getChildByName(panel, "buttonRightDecline");
        okButton = (JButton) FindComponents.getChildByName(panel, "okButton");
        centerScrollbar = (JScrollPane) FindComponents.getChildByName(panel, "centerScrollbar");
        rightScrollbar = (JScrollPane) FindComponents.getChildByName(panel, "rightScrollbar");
        comparisonPane = (JPanel) FindComponents.getChildByName(panel, "comparisonPane");
    }

    private void clickButton(JButton button) {
        ActionEvent clickEvent = new ActionEvent(button, ActionEvent.ACTION_PERFORMED, null);
        Arrays.stream(button.getActionListeners()).forEach(l -> {
            l.actionPerformed(clickEvent);
        });
    }


}
