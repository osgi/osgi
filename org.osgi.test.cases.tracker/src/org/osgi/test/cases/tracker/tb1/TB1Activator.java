/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */

package org.osgi.test.cases.tracker.tb1;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.tracker.service.TestService1;

/**
 * Bundle for exporting packages
 * 
 * @author Ericsson Telecom AB
 */
public class TB1Activator implements BundleActivator {

	/**
	 * Starts the bundle. Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		Hashtable<String, Object> ts1Props = new Hashtable<String, Object>();
		ts1Props.put("name", "TestService1");
		ts1Props.put("version", new Float(1.0));
		ts1Props.put("compatible", new Float(1.0));
		ts1Props.put("description", "TestService 1");

		bc.registerService(TestService1.class.getName(), new TestService1() {
			// empty
		}, ts1Props);
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		// empty
	}
}
