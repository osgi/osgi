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
        testCases.put("Test01", new Test1(this));
        testCases.put("Test02", new Test2(this));
        testCases.put("Test03", new Test3(this));
        testCases.put("Test04", new Test4(this));
        testCases.put("Test05", new Test5(this));
        testCases.put("Test06", new Test6(this));
        testCases.put("Test07", new Test7(this));
        testCases.put("Test08", new Test8(this));
        testCases.put("Test09", new Test9(this));
        testCases.put("Test10", new Test10(this));
        testCases.put("Test11", new Test11(this));
        testCases.put("Test12", new Test12(this));
        //testCases.put("Test13", new Test13(this));
        testCases.put("Test14", new Test14(this));
        //testCases.put("Test15", new Test15(this));
        //testCases.put("Test16", new Test16(this));
        testCases.put("Test17", new Test17(this));

        testCases.put("TestBad01", new TestBad1(this));
        testCases.put("TestBad02", new TestBad2(this));
        //testCases.put("TestBad04", new TestBad4(this));
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
