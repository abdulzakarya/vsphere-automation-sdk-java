/*
 * *******************************************************
 * Copyright VMware, Inc. 2016.  All Rights Reserved.
 * SPDX-License-Identifier: MIT
 * *******************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package vmware.samples.common.authentication;

import java.util.Map;

import javax.net.ssl.TrustManager;
import javax.xml.ws.BindingProvider;

import com.vmware.vim25.InvalidLocaleFaultMsg;
import com.vmware.vim25.InvalidLoginFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;

/**
 * Vim api helper class which provides methods for login/logout using
 * username, password authentication.
 */
public class VimAuthenticationHelper {
    /*
     * Variables of the following types for access to the API methods
     * and to the vSphere inventory.
     * -- ManagedObjectReference for the ServiceInstance on the Server
     * -- VimService for access to the vSphere Web service
     * -- VimPortType for access to methods
     * -- ServiceContent for access to managed object services
     */
    private VimService vimService;
    private VimPortType vimPort;
    private ServiceContent serviceContent;

    private static ManagedObjectReference SVC_INST_REF =
            new ManagedObjectReference();
    public static final String VIM_PATH = "/sdk";

    static {
        /*
         * Set up the manufactured managed object reference for the
         * ServiceInstance.
         */
        SVC_INST_REF.setType("ServiceInstance");
        SVC_INST_REF.setValue("ServiceInstance");
    }

    /**
     * (Service local) disabling of the SSL certificate verification in a CXF context.
     *
     * @param port the service
     * @return Successful execution returns {@code true}; otherwise
     *    {@code false}.
     */
    public static boolean ignoreSslCertCXF(BindingProvider port) {
        Client client = ClientProxy.getClient(port);
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        TLSClientParameters params = conduit.getTlsClientParameters();
        if (params == null) {
            params = new TLSClientParameters();
            conduit.setTlsClientParameters(params);
        }
        params.setTrustManagers(new TrustManager[]{new TrustAllTrustManager()});
        params.setDisableCNCheck(true);
        return true;
    }

    /**
     * Creates a session with the server using username and password
     *
     * @param server hostname or ip address of the server to log in to
     * @param username username for login
     * @param password password for login
     * @param skipServerVerification
     */
    public void loginByUsernameAndPassword(
            String server, String username, String password, boolean skipServerVerification) {
        try {
            String vimSdkUrl = "https://" + server + VIM_PATH;

            /*
             * Create a VimService object to obtain a VimPort binding provider.
             * The BindingProvider provides access to the protocol fields
             * in request/response messages. Retrieve the request context
             * which will be used for processing message requests.
             */
            this.vimService = new VimService();
            this.vimPort = vimService.getVimPort();
            Map<String, Object> ctxt =
                    ((BindingProvider) vimPort).getRequestContext();

            /*
             * ignore SSL certificate verification, CXF style
             */
            if (skipServerVerification) {
                ignoreSslCertCXF((BindingProvider) vimPort);
            }

             /*
              * Store the Server URL in the request context and specify true
              * to maintain the connection between the client and server.
              * The client API will include the Server's HTTP cookie in its
              * requests to maintain the session. If you do not set this to
              * true, the Server will start a new session with each request.
              */
            ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, vimSdkUrl);
            ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);

            // Retrieve the ServiceContent object and login
            this.serviceContent = vimPort.retrieveServiceContent(SVC_INST_REF);

            this.vimPort.login(
                serviceContent.getSessionManager(), username, password, null);

        } catch (InvalidLocaleFaultMsg | InvalidLoginFaultMsg | RuntimeFaultFaultMsg e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs out of the current session.
     */
    public void logout() {
        try {
            this.vimPort.logout(serviceContent.getSessionManager());
        } catch (Exception e) {
            System.out.println(" Connect Failed ");
            e.printStackTrace();
        }
    }

    public VimService getVimService() {
        return this.vimService;
    }

    public VimPortType getVimPort() {
        return this.vimPort;
    }

    public ServiceContent getServiceContent() {
        return this.serviceContent;
    }
}