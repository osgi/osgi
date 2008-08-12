/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.lifecycle.tbc;

import org.osgi.framework.*;

/**
 * Bundle activator for the TestBundleControl.
 * 
 * @author Ericsson Radio Systems AB
 */
public class BundleActivatorI implements BundleActivator {
	TestBundleControl	_tbc;

	/**
	 * Starts the TestBundleControl object.
	 */
	public void start(BundleContext bc) {
		try {
			_tbc = new TestBundleControl(bc);
			_tbc.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops the TestBundleControl.
	 */
	public void stop(BundleContext bc) {
		_tbc.quit();
	}
}
