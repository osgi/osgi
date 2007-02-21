/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.service.ContextSharer;
import org.osgi.test.service.TestCaseLink;
import org.osgi.test.service.TestLogger;

/**
This is the bundle initially installed and started by the TestCase
when started. It performs the various tests and reports back to the
TestCase.<p>

To make your own tests, subclass this class and implement the
testmethods that you need. Then make write your own
{@link #getMethods() getMethods} that returns a list of the method
names that you want to run.<p>

There are also some special methods that you can override if you
have some preparation code that you want to run. The methods are
<ul>
<li>{@link #prepare() prepare}</li>
<li>{@link #unprepare() unprepare}</li>
<li>{@link #setState() setState}</li>
<li>{@link #clearState() clearState}</li>
</ul>

<p>
Assume you have two test-methods of your own,
<code>testSomething()</code> and <code>testSomethingElse()</code>.
These methods are called in the following order.

<pre>
<code>
prepare();

setstate();
testSomething();
clearState();

setstate();
testSomethingElse();
clearState();

unprepare()
</code>
</pre>

 */
public abstract class DefaultTestBundleControl
    extends Logger implements Runnable, BundleActivator, TestLogger {

    private BundleContext           context;
    private TestCaseLink            link;
    private ServiceReference        linkRef;
    private String                  webserver;
    private boolean                 cont = false;
    private long                    timeout;
    private Thread                  thisThread;
    private Registry                mainRegistry;
    private Hashtable               sharedContexts = new Hashtable();

    protected Registry              registry;
    double			progressFactor;
    double			progressOffset;

    public void start(BundleContext context) {
        try {
            this.context = context;
            linkRef = context.getServiceReference(TestCaseLink.class.getName());
            if(linkRef == null)
                log("No TestCaseLink service available, will not run");
            else {
                this.link = (TestCaseLink) context.getService(linkRef);
                if(link == null)
                    log("Could not get TestCaseLink, will not run");
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

            Arrays.sort(methodNames);
        }

        return methodNames;
    }

    public String getWebServer() {
        return webserver;
    }

    public BundleContext getContext() {
        return context;
    }

    public Registry getRegistry() {
        return mainRegistry;
    }




    /**
	 * The checkPrerequisites method is called before the prepare method
	 * and before all the testmethods. It should check so that everything
	 * needed to run the test is available and report if everything is
	 * ok or not. The default implementation returns true but should be
	 * overridden in most cases to do the proper checks.
	 *
	 * @return <code>true</code> if all necessary preparations could be
	 *         made, <code>false</code> otherwise.
	 */
    public boolean checkPrerequisites() {
        return true;
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

        timeout = Integer.parseInt(System.getProperty("org.osgi.test.testcase.timeout", "60000"));
        try {
            webserver = (String) link.receive(timeout);
            mainRegistry = new Registry(context, webserver, this);
            mainRegistry.registerService(TestLogger.class.getName(), this, null);

            if(webserver == null)
                log("Could not receive web server url, exiting");
            else {

                /* Check that everything needed to run the test is there. */
                if(checkPrerequisites() == true) {
                    /* Make nessecary preparations before running the tests */
                    prepare();

                    /* Run all the specified tests */
                    String methods[] = getTestMethods();

                	progressFactor = 100.0  / methods.length;
                    for(int i = 0; cont && i < methods.length; i++) {
                        try {
                        	progressOffset = i * progressFactor;
                            registry = new Registry(context, webserver, this);
                            setState();
                            logMethodBegin(methods[i]);
                            Method     m = getClass().getDeclaredMethod(methods[i], new Class[0]);

                            m.invoke(this, new Object[0]);
                       }
                       catch(InvocationTargetException ite) {
							Throwable t = ite.getTargetException();
							if ( t instanceof AssertionFailedError ) {
								AssertionFailedError assertion = (AssertionFailedError) t;
								log( methods[i], assertion.getMessage() );
							} else {
	                            log("During execution of " + methods[i], t);
	                            t.printStackTrace();
	                            cont = getContinuationAfterError(t);
							}
                        }
                        catch(Throwable e) {
                            log("During execution of " + methods[i], e);
                            e.printStackTrace();
                            cont = getContinuationAfterError(e);
                        }
                        finally {
                        	logMethodEnd(methods[i]);
                        	clearState();
                        	
                        	/* Clean up all install bundles, gotten services and
							registered services during the method call. */
                        	registry.cleanAll();
                        	progress(100);
                        }
                    }

                    /* Undo all that was prepared before running the tests */
                    unprepare();

                    /* Clean up all install bundles, gotten services and
					registered services. */
                    mainRegistry.cleanAll();

                    /* Uninstall all bundles that have been installed during
					the tests */
                    link.send("ready");
                }
                else {
                    /* Indicate that the test didn't run */
                    link.send("norun");
                }
            }
        }
        catch(Throwable e) {
            e.printStackTrace();
            log("During execution of test bundle control " + e);
            try {
				link.send("ready");
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        finally {
            link = null;
            if(cont)
                context.ungetService(linkRef);
            else
                log("Already died");
        }
    }

	public void progress(int i) throws IOException {
		if (i>=100) i = 100;
		else if (i<0) i = 0;
		link.send("" + (int)(progressOffset + progressFactor * i / 100.0));
	}

	/**
	 * Return a flag to indicate if we should continue after this error
	 * or not.
	 */
	
	public boolean getContinuationAfterError( Throwable throwable ) { return false; }
    /*** Assert and logging methods ***/

    public void log(String test) {
        if(link != null && cont)
            try {
				//System.out.println("log: " + test );
                link.log(test);
            }
            catch(IOException e) {
            }
        else
            System.out.println("No Log: " + test);
    }

    /* Don't expose this to the subclasses. */
    private void log(String test, Throwable throwable) {
        log(test + " " + throwable);
    }


    private void logMethodBegin(String methodName) {
        log("[begin=" + methodName + "]");
    }

    private void logMethodEnd(String methodName) {
        log("[end=" + methodName + "]");
        log("");
    }

    public void setReqMarker(String marker) {
        log("[req=" + marker + "]");
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


    /*** Security methods ***/

    public void invokeWithPermissions(String methodName, PermissionInfo[] perms)
            throws Throwable {

        final String contextBundleName = "contextsharer.jar";
        String contextBundleLocation = getWebServer() + contextBundleName;

        /* Install and start the context sharing bundle */
        Bundle contextBundle = mainRegistry.installBundle(contextBundleName);

        /* Get the context sharing service */
        ServiceReference sr = getContext().getServiceReference(ContextSharer.class.getName());
        ContextSharer cs = (ContextSharer) (getContext().getService(sr));

        /* Get the PermissionAdmin service */
        ServiceReference paRef = getContext().getServiceReference(PermissionAdmin.class.getName());
        PermissionAdmin pa = (PermissionAdmin) getContext().getService(paRef);

        /* Set the permissions for the context sharing bundle */
        pa.setPermissions(contextBundleLocation, perms);

        /* Make the context sharer invoke the specified method on this
		object and thereby calling the method with its permissions */
        Method m = this.getClass().getDeclaredMethod(methodName, new Class[0]);
        try {
            cs.invoke(this, m);
        }
        catch(InvocationTargetException e) {
            throw e.getTargetException();
        }

        /* Delete the permissions for the context sharing bundle */
        pa.setPermissions(contextBundleLocation, null);

        /* Uninstall the context sharing bundle */
        mainRegistry.uninstallBundle(contextBundle);
    }

    /*
	 * Gets a specified ContextSharer. Still use "ungetService" to release the
	 * service.
	 */
    public ContextSharer getContextSharer(Bundle bundle) throws Exception {
        ContextSharer sharer = null;

        String filter = "(Bundle-Location=" + bundle.getLocation() + ")";

        sharer = (ContextSharer) getService(ContextSharer.class, filter);
        sharedContexts.put(sharer, bundle);

        return sharer;
    }

    public void releaseContextSharer(ContextSharer sharer) throws Exception {
        ungetService(sharer);
    }

	/**
	 * Log with some separation
	 */
	public void header( String log ) {
		log( "--------------------------------------------------------");
		log( log );
		log( "--------------------------------------------------------");
	}

    /* Proxy methods to the Registry class */
    public Bundle installBundle(String bundleName, boolean start) throws Exception { return mainRegistry.installBundle(bundleName, start); }
    public Bundle installBundle(String bundleName) throws Exception { return mainRegistry.installBundle(bundleName); }
    public boolean isBundleInstalled(String bundleName) { return mainRegistry.isBundleInstalled(bundleName); }
    public void uninstallBundle(Bundle bundleName) throws Exception { mainRegistry.uninstallBundle(bundleName); }
    public void uninstallAllBundles() throws Exception { mainRegistry.uninstallAllBundles(); }
    public Object getService(Class clazz) throws Exception { return mainRegistry.getService(clazz); }
    public Object getService(Class clazz, String filter) throws Exception { return mainRegistry.getService(clazz, filter); }
    public void ungetService(Object service) { mainRegistry.ungetService(service); }
    public void ungetAllServices() { mainRegistry.ungetAllServices(); }
    public boolean serviceAvailable(Class clazz) { return mainRegistry.serviceAvailable(clazz); }
    public boolean securityNeeded(boolean needed) { return mainRegistry.securityNeeded(needed); }
    public void registerService(String clazz, Object service, Dictionary properties) throws Exception {
        mainRegistry.registerService(clazz, service, properties); }
    public void registerService(String clazz, Object service, Dictionary properties, BundleContext bc) throws Exception {
        mainRegistry.registerService(clazz, service, properties, bc); }
    public void registerService(String[] clazz, Object service, Dictionary properties) throws Exception {
        mainRegistry.registerService(clazz, service, properties); }
    public void registerService(String[] clazz, Object service, Dictionary properties, BundleContext bc) throws Exception {
        mainRegistry.registerService(clazz, service, properties, bc); }
    public void unregisterService(Object service) { mainRegistry.unregisterService(service); }
    public void unregisterAllServices() { mainRegistry.unregisterAllServices(); }


}

