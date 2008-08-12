/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc1.tb1;

import org.osgi.framework.*;
import java.util.*;

/**
 * Bundle for exporting packages
 * 
 * @author Ericsson Telecom AB
 */
public class TB1Activator implements BundleActivator {
	BundleContext		bc;
	ServiceRegistration	sr;
	TestService1		ts1;
	Properties			ts1Props;
	ServiceRegistration	tsr1;

	/**
	 * Starts the bundle. Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		try {
			this.bc = bc;
			ts1 = new TestService1Impl();
			ts1Props = new Properties();
			ts1Props.put("name", "TestService1");
			ts1Props.put("version", new Float(1.0));
			ts1Props.put("compatible", new Float(1.0));
			ts1Props.put("description", "TestService 1");
			tsr1 = bc.registerService(TestService1.class.getName(), ts1,
					ts1Props);
		}
		catch (Throwable t) {
			System.err.println("Bundle could not be started: " + t);
		}
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		try {
			tsr1.unregister();
		}
		catch (IllegalStateException e) { /* Ignore */
		}
		tsr1 = null;
		ts1 = null;
	}
}
