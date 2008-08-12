/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.lifecycle.tb8;

import org.osgi.framework.*;
import org.osgi.test.cases.framework.lifecycle.tbc.*;

/**
 * Bundle for the stopped bundle test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class StoppedBC implements BundleActivator {
	/**
	 * Starts the bundle.
	 */
	public void start(BundleContext bc) {
		ServiceReference ref;
		TestBundleControl tbc;
		ref = bc.getServiceReference(TestBundleControl.class.getName());
		tbc = (TestBundleControl) bc.getService(ref);
		tbc.setBundleContext(bc);
		bc.ungetService(ref);
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
	}
}
