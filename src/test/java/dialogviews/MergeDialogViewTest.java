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
import commands.Merge;
import git.GitBranch;
import git.GitData;
import git.exception.GitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

import static dialogviews.FindComponents.getChildByName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class MergeDialogViewTest extends AbstractCommandTest {
    MergeDialogView mdv;
    JButton okButton;
    JButton abortButton;
    JLabel fromLabel;
    JLabel toLabel;
    JComboBox<GitBranch> fromComboBox;

    @BeforeEach
    void setUp() {
        mdv = new MergeDialogView();
        JPanel panel = mdv.getPanel();
        okButton = (JButton) getChildByName(panel, "okButton");
        abortButton = (JButton) getChildByName(panel, "abortButton");
        fromComboBox = (JComboBox<GitBranch>) getChildByName(panel, "fromComboBox");
        fromLabel = (JLabel) getChildByName(panel, "fromLabel");
        toLabel = (JLabel) getChildByName(panel, "toLabel");
    }

    @Override
    public void init() throws IOException, GitAPIException, GitException, URISyntaxException {
        super.init();
        git.branchCreate().setName("testBranch").call();
    }

    @Test
    void testNoSelection() {
        fromComboBox.setSelectedIndex(-1);
        ActionEvent okClicked = new ActionEvent(okButton, ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener actionListener : okButton.getActionListeners()) {
            actionListener.actionPerformed(okClicked);

        }
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);


    }

    @Test
    void testBranchSelectedThenMergeFailure() {
        MockedConstruction<Merge> mmerge = mockConstruction(Merge.class, (mock, context) -> {
            when(mock.execute()).thenReturn(false);
        });
        fromComboBox.setSelectedIndex(0);
        ActionEvent okClicked = new ActionEvent(okButton, ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener actionListener : okButton.getActionListeners()) {
            actionListener.actionPerformed(okClicked);

        }
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);

        mmerge.close();
    }

    @Test
    void updateGitExceptionTest() {
        MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) -> {
            when(mock.getBranches()).thenThrow(new GitException());
        });
        mdv = new MergeDialogView();
        mdv.update();
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
        gitDataMockedConstruction.close();
    }

    @Test
    void updateIOExceptionTest() {
        MockedConstruction<GitData> gitDataMockedConstruction = mockConstruction(GitData.class, (mock, context) -> {
            when(mock.getSelectedBranch()).thenThrow(new IOException());
        });
        mdv = new MergeDialogView();
        mdv.update();
        assertTrue(guiControllerTestable.errorHandlerMSGCalled);
        gitDataMockedConstruction.close();
    }

    @Test
    void metaDataTest() {
        assertNotNull(mdv.getDimension());
        assertNotNull(mdv.getPanel());
        assertNotNull(mdv.getTitle());
    }

}
