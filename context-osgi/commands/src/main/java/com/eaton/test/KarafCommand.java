package com.eaton.test;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.action.Action;
import vmware.samples.tagging.workflow.TaggingWorkflow;

@Command(scope = "cmd", name = "execute", description = "execute a karaf command")
@Service
public class KarafCommand implements Action {

    @Override
    public Object execute() throws Exception {

        String[] args = new String[0];
        TaggingWorkflow.main(args);

        return null;
    }

}