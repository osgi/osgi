package com.nokia.test.doit;

import java.io.File;

import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;


public interface TestRunner {
    
    void setDoIt(DoIt doIt);
    String[] getTestIds();
    TestCaseClass getTest(String testId) throws TestCaseException;

    File getFile(String file);
    ResourceProcessor getRp(String aPid);
    Bundle[] getBundles();

}
