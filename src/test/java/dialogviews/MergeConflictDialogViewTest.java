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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static util.UITestHelper.clickButton;

class MergeConflictDialogViewTest extends AbstractCommandTest {
    GitFileConflict conflict;
    MergeConflictDialogView mcdv;
    private JButton buttonLeftAccept;
    private JButton buttonLeftDecline;
    private JButton buttonRightAccept;
    private JButton buttonRightDecline;
    private JButton okButton;

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
        buttonLeftAccept = (JButton) FindComponents.getChildByName(panel, "buttonLeftAccept");
        buttonLeftDecline = (JButton) FindComponents.getChildByName(panel, "buttonLeftDecline");
        buttonRightAccept = (JButton) FindComponents.getChildByName(panel, "buttonRightAccept");
        buttonRightDecline = (JButton) FindComponents.getChildByName(panel, "buttonRightDecline");
        okButton = (JButton) FindComponents.getChildByName(panel, "okButton");
    }

}
