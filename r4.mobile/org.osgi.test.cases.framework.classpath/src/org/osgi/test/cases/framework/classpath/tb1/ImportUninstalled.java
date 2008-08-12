/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.classpath.tb1;

import org.osgi.framework.*;

/**
 * Bundle for the ImportUninstalledCode test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class ImportUninstalled implements BundleActivator {
	/**
	 * Starts and stops the bundle. (Will never be run anyway.)
	 */
	public void start(BundleContext bc) {
	}

	public void stop(BundleContext bc) {
	}
}
