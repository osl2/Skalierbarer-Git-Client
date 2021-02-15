package git.remoteProvider;

import controller.GUIController;
import dialogviews.UsernamePasswordDialogView;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class CredentialProviderHolder {
private static CredentialProviderHolder INSTANCE = null;
private UsernamePasswordCredentialsProvider provider;
    private CredentialProviderHolder(){
        provider = new UsernamePasswordCredentialsProvider("", "");
    }
    public static CredentialProviderHolder getInstance(){
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
    public void changeProvider(boolean needProv){
        if (needProv) {
            GUIController.getInstance().openDialog(new UsernamePasswordDialogView());
        }
        else {
            provider.clear();
        }
    }

}
