/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.classpath.tb4b;

import org.osgi.framework.*;

/**
 * The importing bundle of the ImportGone test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class ImportGone implements BundleActivator {
	/**
	 * Starts the bundle. Tries to create an object of the imported class
	 * "org.osgi.test.cases.framework.classpath.tb4a.exp.Exported".
	 */
	public void start(BundleContext bc) {
		org.osgi.test.cases.framework.classpath.tb4a.exp.Exported e;
		e = new org.osgi.test.cases.framework.classpath.tb4a.exp.Exported();
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
	}
}
