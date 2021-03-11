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
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class ConfigTest extends AbstractCommandTest {

    @Test
    void executeTest() {

        MockedConstruction<GitFacade> gitFacadeMockedConstruction = mockConstruction(GitFacade.class, (mock, context) ->
        {
            when(mock.setConfigValue(anyString(), anyString())).thenThrow(new AssertionError());
        });

        Config config = new Config();
        assertTrue(config.execute());

        gitFacadeMockedConstruction.close();

        gitFacadeMockedConstruction = mockConstruction(GitFacade.class, (mock, context) ->
        {
            when(mock.setConfigValue(eq("user.name"), anyString())).thenReturn(true);
            when(mock.setConfigValue(eq("user.email"), anyString())).thenReturn(true);
        });

        config.setName("Name");
        config.setEMail("Email");
        assertTrue(config.execute());

        gitFacadeMockedConstruction.close();
    }

    @Test
    void failOnNullTest() {
        Config config = new Config();
        assertTrue(config.execute());

        config.setName(null);
        assertFalse(config.execute());

        config.setName("");
        assertTrue(config.execute());
        config.setEMail(null);
        assertFalse(config.execute());

    }

}
