/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc1.tbc;

import org.osgi.framework.*;

/**
 * Bundle activator for the TestBundleControl.
 * 
 * @author Ericsson Telecom AB
 */
public class BundleActivatorI implements BundleActivator {
	TBC	tbc;

	/**
	 * Starts the TestBundleControl object.
	 */
	public void start(BundleContext bc) {
		try {
			tbc = new TBC(bc);
			tbc.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops the TestBundleControl.
	 */
	public void stop(BundleContext bc) {
		tbc.quit();
	}
}
