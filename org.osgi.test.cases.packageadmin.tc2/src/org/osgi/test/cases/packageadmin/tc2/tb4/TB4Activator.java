/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc2.tb4;

import org.osgi.framework.*;
import org.osgi.test.cases.packageadmin.tc2.tb3.*;

/**
 * Bundle for exporting packages
 * 
 * @author Ericsson Telecom AB
 */
public class TB4Activator implements BundleActivator {
	BundleContext	bc;

	/**
	 * Starts the bundle. Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		this.bc = bc;
		ServiceReference sr = bc
				.getServiceReference("org.osgi.test.cases.packageadmin.tc2.tb3.TestService3");
		TestService3 ts3 = (TestService3) bc.getService(sr);
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
	}
}
