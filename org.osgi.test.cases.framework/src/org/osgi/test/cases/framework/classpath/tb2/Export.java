/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.classpath.tb2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle for the Export test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class Export implements BundleActivator {
	/**
	 * Starts the bundle. Creates an object of the imported class
	 * "org.osgi.test.cases.framework.classpath.tbc.exp.Exported".
	 */
	public void start(BundleContext bc) {
		@SuppressWarnings("unused")
		org.osgi.test.cases.framework.classpath.exported.Exported e;
		e = new org.osgi.test.cases.framework.classpath.exported.Exported();
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
	}
}
