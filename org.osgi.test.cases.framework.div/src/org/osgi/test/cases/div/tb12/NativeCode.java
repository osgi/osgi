/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.div.tb12; // could not rename this package without rebuilding native so/dll

import org.osgi.framework.*;

/**
 * Bundle for the NativeCode test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class NativeCode implements BundleActivator {
	/**
	 * Starts the bundle. Excercises the native code.
	 */
	public void start(BundleContext bc) throws BundleException {
		org.osgi.test.cases.div.tb2.NativeCode.test();
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
	}
}
