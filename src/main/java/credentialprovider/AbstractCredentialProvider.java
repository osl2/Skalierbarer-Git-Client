package credentialprovider;

public abstract class AbstractCredentialProvider {

    public abstract String[] getCredentials(boolean forceNew);
}
