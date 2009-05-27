/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.lifecycle.tb10;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Testing permissions in the framework
 * 
 * @author Ericsson Telecom AB
 */
public class BundleActivatorImpl implements BundleActivator {
	BundleContext	bc;
	TestPermission	tp;

	/**
	 * Starts the bundle.
	 */
	public void start(BundleContext context) {
		bc = context;
		tp = new TestPermission(bc);
		Thread testThread = new Thread(new Runnable(){

			public void run() {
				tp.doTest();
			}}, "TestPermissions");
		testThread.start();
	}

	/**
	 * Stops the bundle. Waits indefinitely.
	 */
	public void stop(BundleContext context) {
		// empty
	}
}
