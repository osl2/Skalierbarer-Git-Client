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

import commands.Pull;
import git.CredentialProviderHolder;
import git.GitBranch;
import git.GitData;
import git.GitRemote;
import git.exception.GitException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the PullDialogView. The user can choose a remote and
 * a branch on this remote to pull.
 */
public class PullDialogView implements IDialogView {


    private JComboBox<String> remoteCombobox;
    private JButton refreshButton;
    private JComboBox<String> branchComboBox;
    private JButton pullButton;
    private JPanel pullPanel;
    private GitData data;
    private List<GitRemote> listOfRemotes;
    private List<GitBranch> listOfRemoteBranches = new ArrayList<>();
    private Pull pull;

    /**
     * Builds the PullDialogView JPanel.
     */
    public PullDialogView() {
        remoteCombobox.addActionListener(e -> {
            listOfRemoteBranches.clear();
            branchComboBox.removeAllItems();
            int index = remoteCombobox.getSelectedIndex();
            if (!nextTry(index)) {
                return;
            }
            for (GitBranch listOfRemoteBranch : listOfRemoteBranches) {
                branchComboBox.addItem(listOfRemoteBranch.getName());
            }
        });
        refreshButton.addActionListener(e -> {
            refresh();
            initPull();
        });
        pullButton.addActionListener(e -> {
            pull = new Pull();
            int remoteIndex = remoteCombobox.getSelectedIndex();
            pull.setRemote(listOfRemotes.get(remoteIndex));
            int branchIndex = branchComboBox.getSelectedIndex();
            pull.setRemoteBranch(listOfRemoteBranches.get(branchIndex));
            pull.execute();
        });
        initPull();
        setNameComponents();
    }

    /**
     * This method is needed in order to execute the GUI tests successfully.
     * Do not change otherwise tests might fail.
     */
    private void setNameComponents() {
        remoteCombobox.setName("remoteCombobox");
        branchComboBox.setName("branchComboBox");
        pullButton.setName("pullButton");
        refreshButton.setName("refreshButton");
    }

    private boolean nextTry(int index) {
        try {
            listOfRemoteBranches = data.getBranches(listOfRemotes.get(index));
            return true;
        } catch (GitException gitException) {
            CredentialProviderHolder.getInstance().changeProvider(true, listOfRemotes.get(index).getName());
            if (CredentialProviderHolder.getInstance().isActive()) {
                return nextTry(index);
            } else {
                CredentialProviderHolder.getInstance().setActive(true);
                return false;
            }
        }
    }

    private void refresh() {
        listOfRemotes.clear();
        listOfRemoteBranches.clear();
        branchComboBox.removeAllItems();
    }

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    @Override
    public String getTitle() {
        return "Pull";
    }

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    @Override
    public Dimension getDimension() {
        return new Dimension(500, 200);
    }

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the shown content
     */
    @Override
    public JPanel getPanel() {
        return pullPanel;
    }

    private void initPull() {
        data = new GitData();
        listOfRemotes = data.getRemotes();
        String[] remoteName = new String[listOfRemotes.size()];
        for (int i = 0; i < listOfRemotes.size(); i++) {
            remoteName[i] = listOfRemotes.get(i).getName();
        }
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(remoteName);
        remoteCombobox.setModel(model);
        remoteCombobox.setSelectedIndex(0);
    }

    @Override
    public void update() {
        // This method is not used because it is not needed.
    }

}
