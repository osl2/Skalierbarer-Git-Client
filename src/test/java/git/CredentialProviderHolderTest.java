package git;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CredentialProviderHolderTest {
  CredentialProviderHolder credentialProviderHolder;
  @BeforeEach
  protected void beforeEach(){
    credentialProviderHolder = CredentialProviderHolder.getInstance();
    credentialProviderHolder.setPassword("password");
    credentialProviderHolder.setUsername("username");
    credentialProviderHolder.setActive(false);
  }

   @Test
  public void setPasswordTest (){
    assertEquals (new UsernamePasswordCredentialsProvider("username", "password").hashCode(), credentialProviderHolder.getProvider().hashCode());
    credentialProviderHolder.setPassword("newPassword");
    assertEquals(new UsernamePasswordCredentialsProvider("username", "newPassword").hashCode(), credentialProviderHolder.getProvider().hashCode());
   }

   @Test
  public void setUsernameTest (){
     assertEquals (new UsernamePasswordCredentialsProvider("username", "password").hashCode(), credentialProviderHolder.getProvider().hashCode());
     credentialProviderHolder.setUsername("newUsername");
     assertEquals(new UsernamePasswordCredentialsProvider("newUsername", "Password").hashCode(), credentialProviderHolder.getProvider().hashCode(),);
   }

   @Test
  public void setActiveTest (){
    assertEquals(false, credentialProviderHolder.isActive());
    credentialProviderHolder.setActive(true);
    assertEquals(true, credentialProviderHolder.isActive());

   }
}
