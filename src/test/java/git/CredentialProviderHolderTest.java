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
package git;

import controller.GUIController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class CredentialProviderHolderTest {

  static GUIControllerTestable guiControllerTestable;
  static MockedStatic<GUIController> mockedController;
  CredentialProviderHolder credentialProviderHolder;

  @BeforeAll
  static void setup() {
    guiControllerTestable = new GUIControllerTestable();
    mockedController = mockStatic(GUIController.class);
    mockedController.when(GUIController::getInstance).thenReturn(guiControllerTestable);
    guiControllerTestable.resetTestStatus();


  }

  @AfterAll
  static void tearDown() {
    mockedController.close();
  }

  @BeforeEach
  protected void beforeEach() {
    credentialProviderHolder = CredentialProviderHolder.getInstance();
    credentialProviderHolder.setPassword("password");
    credentialProviderHolder.setUsername("username");
    credentialProviderHolder.setActive(false);
  }

  @Test
  void setPasswordTest() {
    int hash = credentialProviderHolder.getProvider().hashCode();
    credentialProviderHolder.setPassword("newPassword");
    assertNotEquals(hash, credentialProviderHolder.getProvider().hashCode());
  }

  @Test
  void setUsernameTest() {
    int hash = credentialProviderHolder.getProvider().hashCode();
    credentialProviderHolder.setUsername("newUsername");
    assertNotEquals(hash, credentialProviderHolder.getProvider().hashCode());
  }

  @Test
  void setActiveTest() {
    assertFalse(credentialProviderHolder.isActive());
    credentialProviderHolder.setActive(true);
    assertTrue(credentialProviderHolder.isActive());

  }

  @Test
  void changeProviderTest() {
    credentialProviderHolder.changeProvider(false, "nameForProof");
    assertFalse(guiControllerTestable.openDialogWithListenerCalled);
    credentialProviderHolder.changeProvider(true, "nameForProof");
    assertTrue(guiControllerTestable.openDialogWithListenerCalled);
  }
}
 
