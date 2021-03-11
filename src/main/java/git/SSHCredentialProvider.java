/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.agentproxy.AgentProxyException;
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository;
import com.jcraft.jsch.agentproxy.connector.SSHAgentConnector;
import com.jcraft.jsch.agentproxy.usocket.JNAUSocketFactory;
import org.eclipse.jgit.transport.JschConfigSessionFactory;

import java.util.logging.Logger;

/**
 * Provides the connection between JGit and the system's SSH-Agent
 */
public class SSHCredentialProvider extends JschConfigSessionFactory {
    @Override
    protected void configureJSch(JSch jsch) {
        super.configureJSch(jsch);
        try {
            jsch.setIdentityRepository(new RemoteIdentityRepository(new SSHAgentConnector(new JNAUSocketFactory())));

        } catch (AgentProxyException e) {
            Logger.getGlobal().severe("Couldn't use SSH-Agent: " + e.getMessage());
        }
    }
}
