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

import git.GitFacade;

/**
 * This class is used the change the e-Mail and the name that will
 * be displayed when the user creates a new Commit.
 */
public class Config implements ICommand {
    private String name = "";
    private String eMail = "";

    @Override
    public boolean execute() {
        if (name == null || eMail == null) {
            return false;
        }
        GitFacade facade = new GitFacade();
        boolean retVal = (name.compareTo("") == 0) || facade.setConfigValue("user.name", name);
        retVal &= (eMail.compareTo("") == 0) || facade.setConfigValue("user.email", eMail);
        return retVal;
    }

    /**
     * Set the new name of the author.
     * @param name the name of the author.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the new e-Mail of the author.
     * @param eMail the e-Mail.
     */
    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

}
