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

import controller.GUIController;
import dialogviews.UsernamePasswordDialogView;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Class which creates the CredentialProvider
 */
public class CredentialProviderHolder {
    private static CredentialProviderHolder instance = null;
    private String username;
    private String password;
    private boolean isActive = true;
    private final WindowListener windowListener = new WindowListener() {
        /**
         * {@inheritDoc}
         */
        @Override
        public void windowOpened(WindowEvent e) {
            CredentialProviderHolder.getInstance().setActive(false);
        }

        @Override
        public void windowClosing(WindowEvent e) {
        //Is not needed for this dialogview
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void windowClosed(WindowEvent e) {
            CredentialProviderHolder.getInstance().setActive(true);
        }

        @Override
        public void windowIconified(WindowEvent e) {
        // Is not needed for the dialogview
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            // Is not needed for the dialogview
        }

        @Override
        public void windowActivated(WindowEvent e) {
            // Is not needed for the dialogview
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            // Is not needed for the dialogview
        }
    };

    /**
     * This calss is a singelton, so the Construktor is private
     */
    private CredentialProviderHolder() {
        username = "";
        password = "";
    }

    /**
     * Method to create the Singelton
     *
     * @return Returns the Instance of the CredentialProvider
     */
    public static CredentialProviderHolder getInstance() {
        if (instance == null) {
            instance = new CredentialProviderHolder();
        }
        return instance;
    }

    static void configureTransport(Transport transport) {
        if (transport instanceof SshTransport)
            ((SshTransport) transport).setSshSessionFactory(new SSHCredentialProvider());
        else
            transport.setCredentialsProvider(getInstance().getProvider());

    }

    /**
     * Method to get a UsernamePasswordProvider
     *
     * @return Returns the created provider
     * @deprecated
     */
    @Deprecated(since = "2021-03-10")
    UsernamePasswordCredentialsProvider getProvider() {
        return new UsernamePasswordCredentialsProvider(username, password);
    }

    /**
     * Method to change the Strings for the Provider. Opens the dialogView for Userinput
     *
     * @param needProv     Boolean if there should be a change
     * @param nameForProof Name of the remote, which the provider is for
     */
    public void changeProvider(boolean needProv, String nameForProof) {
        if (needProv) {
            GUIController.getInstance().openDialog(new UsernamePasswordDialogView(nameForProof), windowListener);
        }
    }

    /**
     * Get method for the boolean isActiv
     *
     * @return gets the value of the variable
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Set method for the boolean isActiv
     *
     * @param active new value of Boolean
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Method to set the variable username
     *
     * @param username new value
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Method to set the variable password
     *
     * @param password new value
     */
    public void setPassword(String password) {
        this.password = password;
    }
}

