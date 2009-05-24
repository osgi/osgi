/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.div.tb4;

import org.osgi.framework.*;

/**
 * Bundle for the DoubleManifestTag test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class DoubleManifestTag implements BundleActivator {
	/**
	 * Starts the bundle. (Won't be run anyway, we're only interested in the
	 * manifest tags.)
	 */
	public void start(BundleContext bc) {
		// Create a reference to tbc
		new org.osgi.test.cases.framework.junit.div.DivTests();
	}

	/**
	 * Stops the bundle. (Won't be run anyway, we're only interested in the
	 * manifest tags.)
	 */
	public void stop(BundleContext bc) {
	}
}
