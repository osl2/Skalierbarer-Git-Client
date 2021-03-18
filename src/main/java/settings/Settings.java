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

import com.fasterxml.jackson.annotation.JsonIgnore;
import git.GitAuthor;
import git.GitData;
import git.GitFacade;
import levels.Level;

import java.io.File;

/**
 * Settings Storage
 */
public class Settings extends DataObservable {

    // Modify settings.PersistencyTest if you add or remove a field!
    private static Settings INSTANCE = null;
    private Level level;
    private boolean useTooltips = true;
    private boolean showTreeView = false;
    @JsonIgnore
    private boolean settingsChanged = false;
    private File activeRepositoryPath;

    /**
     * Private Constructor. This class is instantiated by {@see getInstance()}
     * This class is a SINGLETON
     */
    private Settings() {
        // This layout is necessary so that Jackson can create a correctly instantiated class.
        if (INSTANCE == null) {
            INSTANCE = this;
        }
    }

    /**
     * Method to get an instance of the Settings singleton.
     *
     * @return The current Settings object
     */
    public static Settings getInstance() {
        if (INSTANCE == null) {
            new Settings();
        }
        return INSTANCE;
    }

    /**
     * Returns the currently selected Level
     *
     * @return current Level
     */
    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        if (level != this.getLevel()) {
            this.settingsChanged = true;
        }
        this.level = level;
        fireDataChangedEvent();
    }

    /**
     * Returns the current Author-Data for new Commits
     *
     * @return Committer-Identity
     */
    @JsonIgnore
    public GitAuthor getUser() {
        return new GitData().getUser();
    }

    /**
     * Sets the committer identity
     *
     * <p><b>Does not fire a DataChangedEvent</b></p>
     *
     * @param user Committer-Identity
     */
    @JsonIgnore
    public void setUser(GitAuthor user) {
        GitFacade facade = new GitFacade();
        facade.setConfigValue("user.name", user.getName());
        facade.setConfigValue("user.email", user.getEmail());

        // We do not fire a Settings Changed event, as this data will not be saved by us.
    }

    /**
     * The currently selected repository
     *
     * @return the currently active repository
     */
    public File getActiveRepositoryPath() {
        return activeRepositoryPath;
    }

    /**
     * Sets the active repository
     *
     * @param activeRepositoryPath the curernt Repository Path
     */
    public void setActiveRepositoryPath(File activeRepositoryPath) {
        Data.getInstance().storeNewRepositoryPath(activeRepositoryPath);
        if (activeRepositoryPath != this.getActiveRepositoryPath()) {
            this.settingsChanged = true;
        }
        this.activeRepositoryPath = activeRepositoryPath;
        fireDataChangedEvent();

    }

    /**
     * Should the Tree-View be used?
     * NOT IMPLEMENTED!
     *
     * @return true if tree-view should be used.
     */
    public boolean showTreeView() {
        return showTreeView;
    }

    /**
     * Should tooltips be shown?
     *
     * @return true if tooltips should be enabled
     */
    public boolean useTooltips() {
        return useTooltips;
    }

    /**
     * See {@link #useTooltips()}
     *
     * @param useTooltips Should tooltips be shown?
     */
    public void setUseTooltips(boolean useTooltips) {
        if (useTooltips != this.useTooltips) {
            this.settingsChanged = true;
        }
        this.useTooltips = useTooltips;
        fireDataChangedEvent();
    }

    /**
     * See {@link #showTreeView()}
     *
     * @param showTreeView true if tooltips should be enabled
     */
    public void setShowTreeView(boolean showTreeView) {
        if (showTreeView != this.showTreeView) {
            this.settingsChanged = true;
        }
        this.showTreeView = showTreeView;
        fireDataChangedEvent();
    }

    /**
     * Called when the settings have been saved by {@link Persistency}
     */
    void settingsSavedListener() {
        this.settingsChanged = false;
    }

    /**
     * Check if the active Repository exists
     *
     * @return true iff the repository exists
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean activeRepositoryIsValid() {
        return this.activeRepositoryPath != null && this.activeRepositoryPath.isDirectory();
    }
}
