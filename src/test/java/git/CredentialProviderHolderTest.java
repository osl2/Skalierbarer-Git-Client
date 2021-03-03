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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class CredentialProviderHolderTest {

  CredentialProviderHolder credentialProviderHolder;
  @Spy
  GUIController controller;

  @Before
  public void before() {
    controller = GUIController.getInstance();
    MockitoAnnotations.initMocks(this);
  }

  @BeforeEach
  protected void beforeEach() {
    credentialProviderHolder = CredentialProviderHolder.getInstance();
    credentialProviderHolder.setPassword("password");
    credentialProviderHolder.setUsername("username");
    credentialProviderHolder.setActive(false);
  }

  @Test
  public void setPasswordTest() {
    assertEquals(new UsernamePasswordCredentialsProvider("username", "password").hashCode(), credentialProviderHolder.getProvider().hashCode());
    credentialProviderHolder.setPassword("newPassword");
    assertEquals(new UsernamePasswordCredentialsProvider("username", "newPassword").hashCode(), credentialProviderHolder.getProvider().hashCode());
  }

  @Test
  public void setUsernameTest() {
    assertEquals(new UsernamePasswordCredentialsProvider("username", "password").hashCode(), credentialProviderHolder.getProvider().hashCode());
    credentialProviderHolder.setUsername("newUsername");
    assertEquals(new UsernamePasswordCredentialsProvider("newUsername", "Password").hashCode(), credentialProviderHolder.getProvider().hashCode());
  }

  @Test
  public void setActiveTest() {
    assertEquals(false, credentialProviderHolder.isActive());
    credentialProviderHolder.setActive(true);
    assertEquals(true, credentialProviderHolder.isActive());

  }

  @Test
  public void changeProviderTest() {
    credentialProviderHolder.changeProvider(false, "nameForProof");
    Mockito.verify(controller, times(0))
        .openDialog(any(UsernamePasswordDialogView.class), any(WindowListener.class));
    credentialProviderHolder.changeProvider(true, "nameForProof");
    Mockito.verify(controller).openDialog(new UsernamePasswordDialogView("nameForProof"), any(WindowListener.class));
  }
}
