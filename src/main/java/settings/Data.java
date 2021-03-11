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
package settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import commands.*;
import levels.Level;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class holds general purpose data which needs to be persisted.
 */
public class Data extends DataObservable {
    private static Data INSTANCE;

    @JsonProperty("recentlyOpenedRepositories")
    private final LinkedList<File> repoList = new LinkedList<>();
    @JsonProperty("levels")
    private final ArrayList<Level> levels = new ArrayList<>();

    // This layout is necessary so that Jackson can create a correctly instantiated class.
    private Data() {
        if (INSTANCE == null)
            INSTANCE = this;
    }

    /**
     * Singleton
     *
     * @return the only Instance
     */
    public static Data getInstance() {
        if (INSTANCE == null)
            new Data();
        return INSTANCE;
    }

    private void configureDefaultLevels() {
        Add add = new Add();
        Commit commit = new Commit();
        Revert revert = new Revert();
        Init init = new Init();

        Branch branch = new Branch();
        Checkout checkout = new Checkout();
        Merge merge = new Merge();

        Clone clone = new Clone();
        Fetch fetch = new Fetch();
        Pull pull = new Pull();
        Push push = new Push();

        Rebase rebase = new Rebase(null, null);
        Remote remote = new Remote();

        LinkedList<ICommandGUI> commands1 = new LinkedList<>();
        LinkedList<ICommandGUI> commands2 = new LinkedList<>();
        LinkedList<ICommandGUI> commands3 = new LinkedList<>();
        LinkedList<ICommandGUI> commands4 = new LinkedList<>();

        commands1.add(add);
        commands1.add(commit);
        commands1.add(init);
        commands1.add(revert);

        commands2.add(add);
        commands2.add(commit);
        commands2.add(init);
        commands2.add(revert);
        commands2.add(branch);
        commands2.add(checkout);
        commands2.add(merge);

        commands3.add(add);
        commands3.add(commit);
        commands3.add(init);
        commands3.add(revert);
        commands3.add(branch);
        commands3.add(checkout);
        commands3.add(merge);
        commands3.add(clone);
        commands3.add(fetch);
        commands3.add(pull);
        commands3.add(push);

        commands4.add(add);
        commands4.add(commit);
        commands4.add(init);
        commands4.add(revert);
        commands4.add(branch);
        commands4.add(checkout);
        commands4.add(merge);
        commands4.add(clone);
        commands4.add(fetch);
        commands4.add(pull);
        commands4.add(push);
        commands4.add(remote);
        commands4.add(rebase);
        Level levelOne = new Level("Level 1", commands1, 1);
        Level levelTwo = new Level("Level 2", commands2, 2);
        Level levelThree = new Level("Level 3", commands3, 3);
        Level levelFour = new Level("Level 4", commands4, 4);
        levels.add(levelOne);
        levels.add(levelTwo);
        levels.add(levelThree);
        levels.add(levelFour);
    }

    /**
     * Method to get A list of all repositories that have been opened before.
     *
     * @return list of paths ordered by time last opened ascending
     */
    @SuppressWarnings("unchecked")
    public List<File> getRecentlyOpenedRepositories() {
        return (List<File>) repoList.clone();
    }

    /**
     * Stores a new path to a repository.
     *
     * @param path path to a new created git repository;
     */
    public void storeNewRepositoryPath(File path) {
        File absFile = path.getAbsoluteFile();
        if (!repoList.contains(absFile))
            repoList.add(absFile);
    }

    /**
     * Method to get a list of all possible levels in the current configuration.
     *
     * @return immutable ordered List of all possible levels in the current configuration
     */
    public List<Level> getLevels() {
        if (levels.isEmpty()) configureDefaultLevels();
        // Modify settings.PersistencyTest if you add or remove a field!
        return levels;
    }
}
