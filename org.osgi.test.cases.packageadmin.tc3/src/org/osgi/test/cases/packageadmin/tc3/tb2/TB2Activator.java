/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc3.tb2;

import org.osgi.framework.*;

/**
 * Bundle for exporting packages
 * 
 * @author Ericsson Telecom AB
 */
public class TB2Activator implements BundleActivator {
	BundleContext	bc;

	/**
	 * Starts the bundle. Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		this.bc = bc;
		org.osgi.test.cases.packageadmin.tc3.tb1.TestService1 ts = new org.osgi.test.cases.packageadmin.tc3.tb1.TestService1Impl();
		/*
		 * ServiceReference sr =
		 * bc.getServiceReference("org.osgi.test.cases.packageadmin.tc3.tb1.TestService1");
		 * TestService1 ts1 = (TestService1)bc.getService(sr);
		 */
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
	}
}
