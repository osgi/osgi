/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc1.tb2;

import org.osgi.framework.*;
import org.osgi.test.cases.packageadmin.tc1.tb1.*;
import java.util.*;

/**
 * Bundle for exporting packages
 * 
 * @author Ericsson Telecom AB
 */
public class TB2Activator implements BundleActivator {
	BundleContext		bc;
	ServiceRegistration	sr;
	TestService2		ts2;
	Properties			ts2Props;
	ServiceRegistration	tsr2;

	/**
	 * Starts the bundle. Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		try {
			this.bc = bc;
			ts2 = new TestService2Impl();
			ts2Props = new Properties();
			ts2Props.put("name", "TestService2");
			ts2Props.put("version", new Float(1.0));
			ts2Props.put("compatible", new Float(1.0));
			ts2Props.put("description", "TestService 2");
			tsr2 = bc.registerService(TestService2.class.getName(), ts2,
					ts2Props);
		}
		catch (Throwable t) {
			System.err.println("Bundle could not be started: " + t);
		}
		ServiceReference sr = bc
				.getServiceReference("org.osgi.test.cases.packageadmin.tc1.tb1.TestService1");
		TestService1 ts1 = (TestService1) bc.getService(sr);
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		try {
			tsr2.unregister();
		}
		catch (IllegalStateException e) { /* Ignore */
		}
		tsr2 = null;
		ts2 = null;
	}
}
