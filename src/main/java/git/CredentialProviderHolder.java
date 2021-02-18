package git;

import controller.GUIController;
import dialogviews.UsernamePasswordDialogView;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class CredentialProviderHolder {
    private static CredentialProviderHolder INSTANCE = null;
    private UsernamePasswordCredentialsProvider provider;
    private boolean isActive = true;
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
    public void changeProvider(boolean needProv, String nameForProof){
        if (needProv) {
            GUIController.getInstance().openDialog(new UsernamePasswordDialogView(nameForProof));
        }
        else {
            provider.clear();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

