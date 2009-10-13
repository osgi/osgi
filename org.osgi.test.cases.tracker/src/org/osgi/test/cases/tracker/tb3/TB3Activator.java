/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */

package org.osgi.test.cases.tracker.tb3;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.tracker.service.TestService3;

/**
 * Bundle for exporting packages
 * 
 * @author Ericsson Telecom AB
 */
public class TB3Activator implements BundleActivator {
	/**
	 * Starts the bundle. Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		Hashtable ts3Props = new Hashtable();
		ts3Props.put("name", "TestService3");
		ts3Props.put("version", new Float(1.0));
		ts3Props.put("compatible", new Float(1.0));
		ts3Props.put("description", "TestService 3");

		bc.registerService(TestService3.class.getName(), new TestService3() {
			// empty
		}, ts3Props);
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		// empty
	}
}
