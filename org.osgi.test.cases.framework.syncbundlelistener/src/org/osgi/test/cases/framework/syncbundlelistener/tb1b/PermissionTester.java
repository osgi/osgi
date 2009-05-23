/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.syncbundlelistener.tb1b;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

/**
 * Bundle for the CauseFrameworkEvent test.
 * 
 * @author Ericsson Telecom AB
 */
public class PermissionTester implements BundleActivator,
		SynchronousBundleListener {
	/**
	 * Starts the bundle.
	 */
	public void start(BundleContext context) {
		context.addBundleListener(this);
	}

	public void stop(BundleContext context) {
		// empty
	}

	public void bundleChanged(BundleEvent event) {
		// empty
	}
}
