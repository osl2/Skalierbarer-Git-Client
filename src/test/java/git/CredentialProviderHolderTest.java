package git;

import controller.GUIController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import controller.GUIController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import util.GUIControllerTestable;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.awt.event.WindowListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

class CredentialProviderHolderTest {

  static MockedStatic<GUIController> mockedController;
  private static GUIControllerTestable guiControllerTestable;
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
