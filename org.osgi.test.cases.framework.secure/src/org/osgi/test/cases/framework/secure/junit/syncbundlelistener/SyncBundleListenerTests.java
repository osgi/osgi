/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.secure.junit.syncbundlelistener;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Telecom AB
 */
public class SyncBundleListenerTests extends DefaultTestBundleControl {
	
	static final int	timeout	= 2000 * OSGiTestCaseProperties
												.getScaling();
	
	/**
	 * control that a security exception is thrown if we try to add a
	 * synchronous bundle listener without adminPermission
	 */
	public void testPermission() throws Exception {
		Bundle permissionBundle = getContext().installBundle(
				getWebServer() + "syncbundlelistener.tb1a.jar");
		try {
			permissionBundle.start();
		}
		catch (BundleException e) {
			fail("Could not add SynchronousBundleListener", e);
		}
		finally {
			permissionBundle.uninstall();
		}
	}

	/**
	 * control that a security exception is thrown if we try to add a
	 * synchronous bundle listener without adminPermission
	 */
	public void testNoPermission() throws Exception {
		Bundle permissionBundle = getContext().installBundle(
				getWebServer() + "syncbundlelistener.tb1b.jar");
		try {
			permissionBundle.start();
		}
		catch (BundleException e) {
			Throwable cause = e.getCause();
			if (!(cause instanceof SecurityException)) {
				fail("Was able to add SynchronousBundleListener", e);
			}
		}
		finally {
			permissionBundle.uninstall();
		}
	}

}
