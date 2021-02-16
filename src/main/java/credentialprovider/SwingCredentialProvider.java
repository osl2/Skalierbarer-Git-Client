package credentialprovider;

public class SwingCredentialProvider extends AbstractCredentialProvider {
    @Override
    public String[] getCredentials(boolean forceNew) {
        throw new AssertionError("not implemented");
    }
}
