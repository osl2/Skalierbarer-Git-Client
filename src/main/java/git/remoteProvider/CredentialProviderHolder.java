package git.remoteProvider;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class CredentialProviderHolder {
private static CredentialProviderHolder INSTANCE = null;
private UsernamePasswordCredentialsProvider provider;
    private CredentialProviderHolder(){

    }
    public CredentialProviderHolder getINSTANCE(){
        if (INSTANCE == null){
            INSTANCE = new CredentialProviderHolder();
        }
        return INSTANCE;
    }

    public void setProvider(UsernamePasswordCredentialsProvider provider) {
        this.provider = provider;
    }

    UsernamePasswordCredentialsProvider getProvider(){
        return provider;
    }
}
