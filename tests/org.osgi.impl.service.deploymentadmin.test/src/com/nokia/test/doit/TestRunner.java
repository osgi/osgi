package com.nokia.test.doit;


public interface TestRunner {
    
    String[] getTestIds();
    TestCaseClass getTest(String testId) throws TestCaseException;

}
