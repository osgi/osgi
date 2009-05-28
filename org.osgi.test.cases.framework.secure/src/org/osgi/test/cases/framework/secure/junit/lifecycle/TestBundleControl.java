/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.secure.junit.lifecycle;

import org.osgi.framework.Bundle;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.test.cases.framework.secure.lifecycle.servicereferencegetter.ServiceReferenceGetter;
import org.osgi.test.cases.framework.secure.lifecycle.servicereferencegetter.TestResult;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * 
 * The testPermissions test is currently removed because of unknown state. It
 * uses tb10 but this seems to be removed from the upper makefile.
 * 
 * @author Ericsson Radio Systems AB
 */
public class TestBundleControl extends DefaultTestBundleControl {

	/**
	 * Test the permissions in the framework
	 */
	public void testPermissions() throws Throwable {
		Bundle tb;
		Bundle tbPerm;
		tb = installBundle("lifecycle.tb5.jar", false);
		tbPerm = installBundle("lifecycle.tb10.jar", false);
		try {
			tb.start();
			tbPerm.start();
			ServiceReferenceGetter serviceReferenceGetter = (ServiceReferenceGetter) getService(ServiceReferenceGetter.class);
			serviceReferenceGetter.setServiceReference(getContext()
					.getServiceReference(PermissionAdmin.class.getName()));
			Throwable result = ((TestResult) serviceReferenceGetter).get();
			if (result != null)
				throw result;
		}
		finally {
			tbPerm.uninstall();
			if ((tb.getState() & Bundle.UNINSTALLED) == 0) {
				tb.uninstall();
			}
		}
	}
}
