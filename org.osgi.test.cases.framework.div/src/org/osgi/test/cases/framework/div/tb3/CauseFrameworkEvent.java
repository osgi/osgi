/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.div.tb3;

import org.osgi.framework.*;

/**
 * Bundle for the CauseFrameworkEvent test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class CauseFrameworkEvent implements BundleActivator {
	/**
	 * Starts the bundle.
	 */
	public void stop(BundleContext bc) throws BundleException {
		throw new BundleException("Causing a FrameworkEvent in start()");
	}

	public void start(BundleContext context) {
	}
}
