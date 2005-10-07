package com.nokia.test.cases;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;
import com.nokia.test.doit.TestRunner;

public class TestRunnerImpl implements BundleActivator, TestRunner {
    
    private static final String HOME = 
        "../../org.osgi.impl.service.deploymentadmin.test/res/";

    private BundleContext context;
    
    private Hashtable testCases = new Hashtable();
    {
        testCases.put("Test1", new Test1(HOME));
        testCases.put("Test2", new Test2(HOME));
    }
    
    public TestRunnerImpl() throws Exception {
    }

    public void start(BundleContext context) throws Exception {
        this.context = context;
        context.registerService(TestRunner.class.getName(), this, null);
    }

    public void stop(BundleContext context) throws Exception {
    }

    /////////////////////////////////////////////////////////////////////
    
    public String[] getTestIds() {
        String[] ret = new String[testCases.keySet().size()];
        testCases.keySet().toArray(ret);
        return ret;
    }
    
    public TestCaseClass getTest(String testId) throws TestCaseException {
        return (TestCaseClass) testCases.get(testId);
    }

}
