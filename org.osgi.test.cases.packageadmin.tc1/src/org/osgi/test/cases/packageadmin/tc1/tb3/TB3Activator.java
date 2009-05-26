/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc1.tb3;

import org.osgi.framework.*;
import org.osgi.test.cases.packageadmin.tc1.tb2.*;

/**
 * Bundle for exporting packages
 * 
 * @author Ericsson Telecom AB
 */
public class TB3Activator implements BundleActivator {
	/**
	 * Starts the bundle.
	 */
	public void start(BundleContext bc) {
		ServiceReference sr = bc
				.getServiceReference("org.osgi.test.cases.packageadmin.tc1.tb2.TestService2");
		TestService2 ts2 = (TestService2) bc.getService(sr);
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
	}
}
