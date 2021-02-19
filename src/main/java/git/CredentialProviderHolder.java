package git;

import controller.GUIController;
import dialogviews.UsernamePasswordDialogView;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class CredentialProviderHolder {
    private static CredentialProviderHolder INSTANCE = null;
    private UsernamePasswordCredentialsProvider provider;
    private boolean isActive = true;
    private CredentialProviderHolder(){
        provider = new UsernamePasswordCredentialsProvider("", "");
    }
    private WindowListener windowListener = new WindowListener() {
        /**
         * Method to change something
         * @param e
         */
        @Override
        public void windowOpened(WindowEvent e) {
            CredentialProviderHolder.getInstance().setActive(false);
        }

        @Override
        public void windowClosing(WindowEvent e) {

        }

        @Override
        public void windowClosed(WindowEvent e) {
            CredentialProviderHolder.getInstance().setActive(true);
        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }
    };
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
            GUIController.getInstance().openDialog(new UsernamePasswordDialogView(nameForProof),windowListener);
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

