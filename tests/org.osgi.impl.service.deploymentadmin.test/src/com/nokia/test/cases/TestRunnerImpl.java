package com.nokia.test.cases;

import java.io.File;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;

import com.nokia.test.doit.DoIt;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;
import com.nokia.test.doit.TestRunner;

public class TestRunnerImpl implements BundleActivator, TestRunner {
    
    private DoIt doIt;
    
    private Hashtable testCases = new Hashtable();
    {
        testCases.put("Test1", new Test1(this));
        testCases.put("Test2", new Test2(this));
        testCases.put("Test3", new Test3(this));
        testCases.put("Test4", new Test4(this));
        testCases.put("Test5", new Test5(this));
        testCases.put("Test6", new Test6(this));
        testCases.put("Test7", new Test7(this));
    }
    
    public TestRunnerImpl() throws Exception {
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public void start(BundleContext context) throws Exception {
        context.registerService(TestRunner.class.getName(), this, null);
    }

    public void stop(BundleContext context) throws Exception {
    }

    /////////////////////////////////////////////////////////////////////
    
    public void setDoIt(DoIt doIt) {
        this.doIt = doIt;
    }
    
    public String[] getTestIds() {
        String[] ret = new String[testCases.keySet().size()];
        testCases.keySet().toArray(ret);
        return ret;
    }
    
    public TestCaseClass getTest(String testId) throws TestCaseException {
        return (TestCaseClass) testCases.get(testId);
    }

    public File getFile(String file) {
        return doIt.getFile(file);
    }
    
    public ResourceProcessor getRp(String pid) {
        return doIt.getRp(pid);
    }

    public Bundle[] getBundles() {
        return doIt.getBundles();
    }

}
