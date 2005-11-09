package org.osgi.test.cases.deploymentadmin.tc2.tbc.util;

import java.security.AccessControlContext;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;


public interface TestingSecurityResourceProcessor extends ResourceProcessor {
    
    public void setTestDoPrivileged(boolean value);
    public void setAccessControlContext(AccessControlContext ctx);
}
