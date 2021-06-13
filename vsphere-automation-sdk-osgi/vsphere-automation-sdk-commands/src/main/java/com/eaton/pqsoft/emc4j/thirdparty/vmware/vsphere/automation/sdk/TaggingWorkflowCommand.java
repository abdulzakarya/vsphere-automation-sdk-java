package com.eaton.pqsoft.emc4j.thirdparty.vmware.vsphere.automation.sdk;

import com.vmware.cis.Session;
import vmware.samples.common.StaticHelper;
import com.vmware.vapi.bindings.StubConfiguration;
import com.vmware.vapi.bindings.StubFactory;
import com.vmware.vapi.cis.authn.SecurityContextFactory;
import com.vmware.vapi.core.ExecutionContext;
import com.vmware.vapi.protocol.HttpConfiguration;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.action.Action;


@Command(scope = "vsphere", name = "tagging", description = "run tagging workflow")
@Service
public class TaggingWorkflowCommand implements Action {

    @Override
    public Object execute() throws Exception {

        String server = "vcenter67.mbt.lab.etn.com";
        String username = "administrator@vsphere.local";
        String password = "Manager01!";

        HttpConfiguration httpConfig = StaticHelper.buildHttpConfiguration();

        StubFactory stubFactory = StaticHelper.createApiStubFactory(server, httpConfig);

        // Create a security context for username/password authentication
        ExecutionContext.SecurityContext securityContext =
                SecurityContextFactory.createUserPassSecurityContext(
                        username, password.toCharArray());

        // Create a stub configuration with username/password security context
        StubConfiguration stubConfig = new StubConfiguration(securityContext);

        // Create a session stub using the stub configuration.
        Session session =
                stubFactory.createStub(Session.class, stubConfig);

        // Login and create a session
        char[] sessionId = session.create();

        System.out.println(sessionId);

        return null;
    }


}
