/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.lifecycle.tb6a;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle for the normal update test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class NormalUpdate implements BundleActivator {
	/**
	 * Starts the bundle.
	 */
	public void start(BundleContext bc) {
		// empty
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		// empty
	}
}
