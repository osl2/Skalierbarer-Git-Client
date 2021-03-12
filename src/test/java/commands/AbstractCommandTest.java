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

import controller.GUIController;
import git.AbstractGitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;

import static org.mockito.Mockito.mockStatic;

public abstract class AbstractCommandTest extends AbstractGitTest {
    protected static GUIControllerTestable guiControllerTestable;
    protected static MockedStatic<GUIController> mockedController;

    @BeforeEach
    protected void setup() {
        guiControllerTestable = new GUIControllerTestable();
        mockedController = mockStatic(GUIController.class);
        mockedController.when(GUIController::getInstance).thenReturn(guiControllerTestable);
        guiControllerTestable.resetTestStatus();
    }

    @AfterEach
    protected void tearDownCommandTest() {
        mockedController.close();
    }


}
