package com.eaton.test;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.action.Action;

@Command(scope = "cmd", name = "execute", description = "execute a karaf command")
@Service
public class KarafCommand implements Action {

    @Override
    public Object execute() throws Exception {
        System.out.println("hello world!");
        return null;
    }

}