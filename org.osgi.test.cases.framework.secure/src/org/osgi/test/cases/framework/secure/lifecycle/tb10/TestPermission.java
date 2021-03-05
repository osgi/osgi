/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.framework.secure.lifecycle.tb10;

import static junit.framework.TestCase.assertNotNull;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.test.cases.framework.secure.lifecycle.servicereferencegetter.ServiceReferenceGetter;
import org.osgi.test.cases.framework.secure.lifecycle.servicereferencegetter.TestResult;
import org.osgi.test.cases.framework.secure.lifecycle.tb5.EventTest;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * Testing permissions in the framework
 * 
 * @author Ericsson Telecom AB
 */
public class TestPermission implements ServiceReferenceGetter, TestResult, BundleActivator {
	ServiceReference< ? >	_permServiceRef	= null;
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
			assertNotNull("Cannot test without security manager",
					System.getSecurityManager());
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
	public synchronized void setServiceReference(
			ServiceReference< ? > serviceReference) {
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
			TestCase.fail("Didn't get security exception in stop(): FAIL");
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
			TestCase.fail("Didn't get security exception in update(): FAIL");
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
			TestCase.fail("Didn't get security exception in start(): FAIL");
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
			TestCase.fail("Didn't get security exception in uninstall(): FAIL");
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
			TestCase.fail(
					"Didn't get security exception in getHeaders(): FAIL");
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
			TestCase.fail(
					"Didn't get security exception in getLocation(): FAIL");
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
			TestCase.fail(
					"Were able to register service without having permissions: FAIL");
		}
		catch (SecurityException se) {
			// do nothing; PASS.
		}
		catch (Exception e) {
			fail("Exception in testBundleContextPermissions: FAIL", e);
		}
		//getReference
		try {
			ServiceReference<PermissionAdmin> permissionServiceRef = bc
					.getServiceReference(PermissionAdmin.class);
			if (permissionServiceRef != null) {
				TestCase.fail(
						"Were able to get service refernce to permission admin without permission: FAIL");
			}
		}
		catch (Exception e) {
			fail("Exception in testBundleContextPermissions: FAIL", e);
		}
		//getService
		//We got the service refernce earlier when tb10 had allPermissions
		try {
			assertNotNull("ServiceReference is null!", _permServiceRef);
			bc.getService(_permServiceRef);
			TestCase.fail(
					"Were able to get permission admin without permission: FAIL");
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
