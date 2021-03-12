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
package commands;

import git.GitBranch;
import git.GitData;
import git.GitFileConflict;
import git.exception.GitException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import views.AddCommitView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MergeTest extends AbstractCommandTest {

    @Test
    void metaDataTest() {
        Merge merge = new Merge();
        GitBranch mockBranch = mock(GitBranch.class);
        when(mockBranch.getName()).thenReturn("MockBranch");

        assertNotNull(merge.getName());
        assertNotNull(merge.getDescription());
        assertNull(merge.getCommandLine());
        merge.setSourceBranch(mockBranch);
        assertEquals("git merge MockBranch", merge.getCommandLine());

        merge.onButtonClicked();
        assertTrue(guiControllerTestable.openDialogCalled);
    }

    @Test
    void executeErrorTest() throws GitException {
        GitBranch mockSrcBranch = mock(GitBranch.class);
        GitBranch mockDestBranch = mock(GitBranch.class);
        when(mockSrcBranch.getName()).thenReturn("MockSrcBranch");
        when(mockSrcBranch.merge(anyBoolean())).thenReturn(new ArrayList<>());
        when(mockDestBranch.getName()).thenReturn("MockDestBranch");

        MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) ->
        {
            // cause dest != selected error.
            when(mock.getSelectedBranch()).thenReturn(mockSrcBranch);
        });
        Merge merge = new Merge();
        assertFalse(merge.execute());

        merge.setSourceBranch(mockSrcBranch);
        assertFalse(merge.execute());

        // Src must not equal Destination!
        merge.setDestinationBranch(mockSrcBranch);
        assertFalse(merge.execute());

        merge.setDestinationBranch(mockDestBranch);

        // Dest != selected
        assertFalse(merge.execute());
        gitDataMockedConstruction.close();
        gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) ->
        {
            // cause dest != selected error.
            when(mock.getSelectedBranch()).thenReturn(mockDestBranch);
        });
        // Dest == selected, List is empty
        assertTrue(merge.execute());
        assertFalse(guiControllerTestable.openDialogCalled);
        gitDataMockedConstruction.close();
    }


    @Test
    void conflictApplyTest() throws GitException, IOException {
        GitBranch mockSrcBranch = mock(GitBranch.class);
        GitBranch mockDestBranch = mock(GitBranch.class);
        GitFileConflict conflict = mock(GitFileConflict.class);
        when(conflict.apply()).thenReturn(false).thenReturn(true);
        when(mockSrcBranch.getName()).thenReturn("MockSrcBranch");
        when(mockSrcBranch.merge(anyBoolean())).thenReturn(Collections.singletonList(conflict));
        when(mockDestBranch.getName()).thenReturn("MockDestBranch");

        MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) ->
        {
            when(mock.getSelectedBranch()).thenReturn(mockDestBranch);
            when(mock.getStoredCommitMessage()).thenReturn(null);
        });
        MockedConstruction<AddCommitView> acvMockedConstruction = mockConstruction(AddCommitView.class, (mock, context) ->
        {
        });
        Merge merge = new Merge(mockSrcBranch, mockDestBranch);

        assertFalse(merge.execute());
        assertTrue(guiControllerTestable.openDialogCalled);
        guiControllerTestable.resetTestStatus();
        assertTrue(merge.execute());
        assertTrue(guiControllerTestable.openViewCalled);

        gitDataMockedConstruction.close();
        acvMockedConstruction.close();
    }

}
