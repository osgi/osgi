/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Vector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.service.TestLogger;

public abstract class DefaultTestBundleHelper 
    extends Logger implements Runnable, BundleActivator {

    protected BundleContext           context;
    private boolean                 cont = false;
    private Thread                  thisThread;
    private ServiceReference        testLoggerRef;
    private TestLogger              testLogger;

    public void start(BundleContext context) {
        try {
            this.context = context;
            testLoggerRef = context.getServiceReference(TestLogger.class.getName());
            if(testLoggerRef == null) {
                log("No TestLogger service available, will not run");
            }
            else {
                testLogger = (TestLogger) context.getService(testLoggerRef);
                if(testLogger == null)
                    log("Could not get TestLogger, will not run");
                else {
                    cont = true;
                    thisThread = new Thread(this);
                    thisThread.start();
                }
            }
        }
        catch(Throwable e) {
            log("Exception during start()", e);
        }
    }

    public void stop(BundleContext context) {
        cont = false;
    }

    /**
     * Returns a list containing the names of the testmethods that 
     * should be called.
     */
    protected String[] getMethods() {
        return null;
    };

    private String[] getTestMethods() {
        String[] methodNames = getMethods();

        /* If there are no method list specified in the subclass, 
         find all methods starting with "test" and call them in
         any order. */
        if(methodNames == null) {
            Method[] methods = getClass().getDeclaredMethods();
            Vector nameVector = new Vector();

            /* Find all "test"-methods that needs no parameters */
            for(int i = 0; i < methods.length; i++) {
                if(methods[i].getName().startsWith("test")) {
                    if(methods[i].getParameterTypes().length == 0) {
                        nameVector.addElement(methods[i].getName());
                    }
                }
            }

            /* Turn the name vector into an array of Strings */
            methodNames = new String[nameVector.size()];
            for(int i = 0; i < methodNames.length; i++) {
                methodNames[i] = (String) nameVector.elementAt(i);
            }
        }

        return methodNames;
    }

    public BundleContext getContext() {
        return context;
    }

    /**
     * The prepare method is called once before running all the tests 
     * returned by getMethods() and should do all preparations needed
     * to run the test case. If the preparations are not possible to do,
     * (i.e. a needed service is missing) and the test can run due to
     * that, this method should return <code>false</code>.
     *
     * @return <code>true</code> if all necessary preparations could be 
     *         made, <code>false</code> otherewise.
     *
     */
    protected void prepare() throws Exception {
    }

    /**
     * The unprepare method is is called once after running all the tests 
     * returned by getMethods(). Override this to do things after 
     * all tests have been run.
     */
    protected void unprepare() throws Exception {
    }

    /**
     * The setState method is called before each and every testmethod
     * Override this to set a specific state before each testmethod is 
     * called.
     */
    protected void setState() throws Exception {
    }

    /**
     * The clearState method can be used to undo whatever the setState
     * did. It is called after each testmethod.
     */
    protected void clearState() throws Exception {
    }


    /**
     This function performs the tests.
     */
    public void run() {
        try {
            /* Make nessecary preparations before running the tests */
            prepare();
                
            /* Run all the specified tests */
            String methods[] = getTestMethods();
    
            for(int i = 0; cont && i < methods.length; i++) {
                try {
                    setState();
                    logMethodBegin(methods[i]);
                    Method     m = getClass().getDeclaredMethod(methods[i], new Class[0]);

                    m.invoke(this, new Object[0]);
                    logMethodEnd(methods[i]);
                    clearState();
                }
                catch(InvocationTargetException ite) {
                    log("During execution of " + methods[i], ite.getTargetException());
                    ite.printStackTrace();
                    cont = false;

                }
                catch(Throwable e) {
                    log("During execution of " + methods[i], e);
                    e.printStackTrace();
                    cont = false;
                }
            }
    
            /* Undo all that was prepared before running the tests */
            unprepare();
        }
        catch(Throwable e) {
            e.printStackTrace();
            log("During execution of test bundle control " + e);
        }
        finally {
            testLogger = null;
            if(cont)
                context.ungetService(testLoggerRef);
            else
                log("Already died");
        }
    }


    /*** Assert and logging methods ***/

    public void log(String test) {
        if(testLogger != null && cont)
            testLogger.log(test);
        else
            System.out.println("No Log: " + test);
    }

    /* Don't expose this to the subclasses. */
    private void log(String test, Throwable throwable) {
        log(test + " " + throwable);
    }


    private void logMethodBegin(String methodName) {
        log("[method name=\"" + methodName + "\"]");
    }

    private void logMethodEnd(String methodName) {
        log("[/method]");
        log("");
    }

    public void setReqMarker(String marker) {
        log("[req name=\"" + marker + "\"/]");
    }

    // public void clearReqMarker 
    //  -   Maybe useful method??? If the Target (this class) keeps 
    //      track of the req-markers and AssertionErrors are thrown, 
    //      then it could present method and the eventual broken requirement.
    //      If added, it should be automatically cleared at the end of
    //      a test method.



    /*** Convenience methods ***/

    /** 
     * Convenience method to convert an array to a String. The method
     * converts each element in the array to a string so the usefulness 
     * of the output depends on if the elements has useful toString()
     * methods.
     *
     * @param array the array to be converted
     * @return a String formatted as 
     *         <code>"{ element1string, element2string, ... }"</code>, 
     *         or "null" if <code>array</code> was <code>null</code>.
     */
    public static String arrayToString(Object[] array) {
        return arrayToString(array, false);
    }

    public static String arrayToString(Object[] array, boolean sort) {
        StringBuffer buf = new StringBuffer();

        if(sort) {
            Arrays.sort(array);
        }

        if(array != null) {
            buf.append("{ ");
            for(int i = 0; i < array.length; i++) {
                buf.append(array[i]);
                if(i + 1 < array.length) {
                    buf.append(", ");
                }
            }

            buf.append(" }");

        }
        else {
            buf.append("null");
        }

        return buf.toString();
    }


}

