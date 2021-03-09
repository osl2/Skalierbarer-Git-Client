package git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.agentproxy.AgentProxyException;
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository;
import com.jcraft.jsch.agentproxy.connector.SSHAgentConnector;
import com.jcraft.jsch.agentproxy.usocket.JNAUSocketFactory;
import org.eclipse.jgit.transport.JschConfigSessionFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SSHCredentialProvider extends JschConfigSessionFactory {
    @Override
    protected void configureJSch(JSch jsch) {
        super.configureJSch(jsch);
        try {
            jsch.setIdentityRepository(new RemoteIdentityRepository(new SSHAgentConnector(new JNAUSocketFactory())));

        } catch (AgentProxyException e) {
            Logger.getGlobal().log(Level.SEVERE, "Couldn't use SSH-Agent: {}", e.getMessage());
        }
    }
}
