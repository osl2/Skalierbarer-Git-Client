package git;

import controller.GUIController;
import dialogviews.UsernamePasswordDialogView;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.awt.event.WindowListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

class CredentialProviderHolderTest {

  CredentialProviderHolder credentialProviderHolder;
  @Spy
  GUIController controller;


  @BeforeEach
  protected void beforeEach() {
    controller = GUIController.getInstance();
    MockitoAnnotations.initMocks(this);
    credentialProviderHolder = CredentialProviderHolder.getInstance();
    credentialProviderHolder.setPassword("password");
    credentialProviderHolder.setUsername("username");
    credentialProviderHolder.setActive(false);
  }

  @Test
   void setPasswordTest() {
    assertEquals(new UsernamePasswordCredentialsProvider("username", "password").hashCode(), credentialProviderHolder.getProvider().hashCode());
    credentialProviderHolder.setPassword("newPassword");
    assertEquals(new UsernamePasswordCredentialsProvider("username", "newPassword").hashCode(), credentialProviderHolder.getProvider().hashCode());
  }

  @Test
   void setUsernameTest() {
    assertEquals(new UsernamePasswordCredentialsProvider("username", "password").hashCode(), credentialProviderHolder.getProvider().hashCode());
    credentialProviderHolder.setUsername("newUsername");
    assertEquals(new UsernamePasswordCredentialsProvider("newUsername", "Password").hashCode(), credentialProviderHolder.getProvider().hashCode());
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
    Mockito.verify(controller, times(0))
        .openDialog(any(UsernamePasswordDialogView.class), any(WindowListener.class));
    credentialProviderHolder.changeProvider(true, "nameForProof");
    controller = GUIController.getInstance();
    Mockito.verify(controller).openDialog(eq(new UsernamePasswordDialogView("nameForProof")), any(WindowListener.class));
  }
}
