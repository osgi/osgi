/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc2.tb4;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.packageadmin.tc2.tb3.TestService3;

/**
 * Bundle for exporting packages
 * 
 * @author Ericsson Telecom AB
 */
public class TB4Activator implements BundleActivator {

	/**
	 * Starts the bundle. Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		ServiceReference sr = bc.getServiceReference(TestService3.class
				.getName());
        if (sr != null) {
            TestService3 ts3 = (TestService3) bc.getService(sr);
        }
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		// empty
	}
}
