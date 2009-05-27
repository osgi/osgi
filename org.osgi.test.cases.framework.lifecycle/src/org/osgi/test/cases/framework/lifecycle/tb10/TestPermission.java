/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.lifecycle.tb10;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.osgi.framework.*;
import org.osgi.service.permissionadmin.*;
import org.osgi.test.cases.framework.lifecycle.servicereferencegetter.*;
import org.osgi.test.cases.framework.lifecycle.tb5.*;
import org.osgi.test.cases.framework.lifecycle.tbc.TestResult;

/**
 * Testing permissions in the framework
 * 
 * @author Ericsson Telecom AB
 */
public class TestPermission implements ServiceReferenceGetter, TestResult, BundleActivator {
	ServiceReference	_permServiceRef	= null;
	boolean				refSet			= false;
	BundleContext		bc;
	Throwable			_result 		= null;
	boolean				_resultSet		= false;

	/**
	 * Starts the bundle.
	 */
	TestPermission(BundleContext context) {
		bc = context;
		try {
			bc.registerService(new String[] {TestResult.class.getName(), ServiceReferenceGetter.class.getName()}, this,
					null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void doTest() {
		//get the log service
		try {
			Assert.assertNotNull("Cannot test without security manager", System.getSecurityManager());
			//Wait for the service reference to be set
			while (refSet == false)
				wait(1000);
			testBundlePermissions();
			testBundleContextPermissions();
		}
		catch (Throwable t) {
			_result = t;
		}
		_resultSet = true;
		notifyAll();
	}

	public synchronized Throwable get() {
		while (!_resultSet) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return _result;
	}

	/**
	 * get the reference to the permission admin service, which we don't have
	 * permission to get ourselves
	 */
	public synchronized void setServiceReference(ServiceReference serviceReference) {
		_permServiceRef = serviceReference;
		refSet = true;
		notifyAll();
	}

	void testBundlePermissions() {
		Bundle bundle = (bc.getServiceReference(EventTest.class.getName()))
				.getBundle();
		//stop
		try {
			bundle.stop();
			Assert.fail("Didn't get security exception in stop(): FAIL");
		}
		catch (SecurityException se) {
			// do nothing; PASS.
		}
		catch (Exception e) {
			fail("Unexpected exception in stop(): FAIL ", e);
		}
		//update
		try {
			bundle.update();
			Assert.fail("Didn't get security exception in update(): FAIL");
		}
		catch (SecurityException se) {
			// do nothing; PASS.
		}
		catch (Exception e) {
			fail("Unexpected exception in update(): FAIL ", e);
		}
		//start
		try {
			bundle.start();
			Assert.fail("Didn't get security exception in start(): FAIL");
		}
		catch (SecurityException se) {
			// do nothing; PASS
		}
		catch (Exception e) {
			fail("Unexpected exception in start(): FAIL ", e);
		}
		//uninstall
		try {
			bundle.uninstall();
			Assert.fail("Didn't get security exception in uninstall(): FAIL");
		}
		catch (SecurityException se) {
			// do nothing; PASS
		}
		catch (Exception e) {
			fail("Unexpected exception in uninstall(): FAIL ", e);
		}
		//getHeaders
		try {
			bundle.getHeaders();
			Assert.fail("Didn't get security exception in getHeaders(): FAIL");
		}
		catch (SecurityException se) {
			// do nothing; PASS
		}
		catch (Exception e) {
			fail("Unexpected exception in getHeaders(): FAIL ", e);
		}
		//getLocation
		try {
			bundle.getLocation();
			Assert.fail("Didn't get security exception in getLocation(): FAIL");
		}
		catch (SecurityException se) {
			// do nothing; PASS
		}
		catch (Exception e) {
			fail("Unexpected exception in getLocation(): FAIL ", e);
		}
	}

	void testBundleContextPermissions() {
		//registerService
		try {
			bc.registerService(BundleActivator.class.getName(), this, null);
		}
		catch (Exception e) {
			fail("Exception in testBundleContextPermissions: FAIL", e);
		}
		try {
			bc.registerService(TestPermission.class.getName(), this, null);
			Assert.fail("Were able to register service without having permissions: FAIL");
		}
		catch (SecurityException se) {
			// do nothing; PASS.
		}
		catch (Exception e) {
			fail("Exception in testBundleContextPermissions: FAIL", e);
		}
		//getReference
		try {
			ServiceReference permissionServiceRef = bc
					.getServiceReference(PermissionAdmin.class.getName());
			if (permissionServiceRef != null) {
				Assert.fail("Were able to get service refernce to permission admin without permission: FAIL");
			}
		}
		catch (Exception e) {
			fail("Exception in testBundleContextPermissions: FAIL", e);
		}
		//getService
		//We got the service refernce earlier when tb10 had allPermissions
		try {
			Assert.assertNotNull("ServiceReference is null!", _permServiceRef);
			bc.getService(_permServiceRef);
			Assert.fail("Were able to get permission admin without permission: FAIL");
		}
		catch (SecurityException se) {
			// do nothing; PASS
		}
		catch (Exception e) {
			fail("Exception in testBundleContextPermissions: FAIL", e);
		}
	}

	public static void fail(String message, Throwable t) {
		AssertionFailedError e = new AssertionFailedError(message + ": "
				+ t.getMessage());
		e.initCause(t);
		throw e;
	}

	public void start(BundleContext context) throws Exception {
		// do nothing
	}

	public void stop(BundleContext context) throws Exception {
		// do nothing
		
	}
}
