/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.lifecycle.tb10;

import org.osgi.framework.*;
import org.osgi.service.permissionadmin.*;
import org.osgi.test.cases.framework.lifecycle.servicereferencegetter.*;
import org.osgi.test.cases.framework.lifecycle.tb5.*;
import org.osgi.test.cases.logproxy.*;

/**
 * Testing permissions in the framework
 * 
 * @author Ericsson Telecom AB
 */
public class TestPermission extends Thread implements ServiceReferenceGetter,
		BundleActivator {
	ServiceReference	_linkRef;
	ServiceReference	_permServiceRef	= null;
	boolean				refSet			= false;
	LogProxy			_link;
	BundleContext		bc;
	boolean				_continue		= true;

	/**
	 * Starts the bundle.
	 */
	TestPermission(BundleContext context) {
		bc = context;
		try {
			bc.registerService(ServiceReferenceGetter.class.getName(), this,
					null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//dummy functions
	public void start(BundleContext context) {
	};

	public void stop(BundleContext context) {
	};

	public synchronized void run() {
		//get the log service
		try {
			_linkRef = bc.getServiceReference(LogProxy.class.getName());
			if (_linkRef != null) {
				System.out.println("We got a link reference:" + _linkRef);
				_link = (LogProxy) bc.getService(_linkRef);
				if (_link != null) {
					//Wait for the service reference to be set
					while (refSet == false)
						wait(1000);
					testBundlePermissions();
					testBundleContextPermissions();
				}
				else
					System.out.println("Didn't get a link");
			}
			else {
				System.out.println("Didn't get a service ref");
			}
		}
		catch (Exception e) {
			System.out.println("exception in start of test permission");
			e.printStackTrace();
		}
		quit();
	}

	/**
	 * get the reference to the permission admin service, which we don't have
	 * permission to get ourselves
	 */
	public void setServiceReference(ServiceReference serviceReference) {
		_permServiceRef = serviceReference;
		refSet = true;
	}

	void testBundlePermissions() {
		Bundle bundle = (bc.getServiceReference(EventTest.class.getName()))
				.getBundle();
		SecurityManager sm = System.getSecurityManager();
		//stop
		try {
			bundle.stop();
			if (sm == null)
				_link
						.log(
								"Got security exception in stop(), as expected or no security manager present",
								"OK");
			else
				_link.log("Didn't get security exception in stop()", "FAIL");
		}
		catch (SecurityException se) {
			_link
					.log(
							"Got security exception in stop(), as expected or no security manager present",
							"OK");
		}
		catch (Exception e) {
			_link.log("Unexpected exception in stop()", "FAIL");
			e.printStackTrace();
		}
		//update
		try {
			bundle.update();
			if (sm == null)
				_link
						.log(
								"Got security exception in update(), as expected or no security manager present",
								"OK");
			else
				_link.log("Didn't get security exception in update()", "FAIL");
		}
		catch (SecurityException se) {
			_link
					.log(
							"Got security exception in update(), as expected or no security manager present",
							"OK");
		}
		catch (Exception e) {
			_link.log("Unexpected exception in update()", "FAIL");
			e.printStackTrace();
		}
		//start
		try {
			bundle.start();
			if (sm == null)
				_link
						.log(
								"Got security exception in start(), as expected or no security manager present",
								"OK");
			else
				_link.log("Didn't get security exception in start()", "FAIL");
		}
		catch (SecurityException se) {
			_link
					.log(
							"Got security exception in start(), as expected or no security manager present",
							"OK");
		}
		catch (Exception e) {
			_link.log("Unexpected exception in start()", "FAIL");
			e.printStackTrace();
		}
		//uninstall
		try {
			bundle.uninstall();
			if (sm == null)
				_link
						.log(
								"Got security exception in uninstall(), as expected or no security manager present",
								"OK");
			else
				_link.log("Didn't get security exception in uninstall()",
						"FAIL");
		}
		catch (SecurityException se) {
			_link
					.log(
							"Got security exception in uninstall(), as expected or no security manager present",
							"OK");
		}
		catch (Exception e) {
			_link.log("Unexpected exception in uninstall()", "FAIL");
			e.printStackTrace();
		}
		//getHeaders
		try {
			bundle.getHeaders();
			if (sm == null)
				_link
						.log(
								"Got security exception in getHeaders(), as expected or no security manager present",
								"OK");
			else
				_link.log("Didn't get security exception in getHeaders()",
						"FAIL");
		}
		catch (SecurityException se) {
			_link
					.log(
							"Got security exception in getHeaders(), as expected or no security manager present",
							"OK");
		}
		catch (Exception e) {
			_link.log("Unexpected exception in getHeaders()", "FAIL");
			e.printStackTrace();
		}
		//getLocation
		try {
			bundle.getLocation();
			if (sm == null)
				_link
						.log(
								"Got security exception in getLocation(), as expected or no security manager present",
								"OK");
			else
				_link.log("Didn't get security exception in getLocation()",
						"FAIL");
		}
		catch (SecurityException se) {
			_link
					.log(
							"Got security exception in getLocation(), as expected or no security manager present",
							"OK");
		}
		catch (Exception e) {
			_link.log("Unexpected exception in getLocation()", "FAIL");
			e.printStackTrace();
		}
	}

	void testBundleContextPermissions() {
		SecurityManager sm = System.getSecurityManager();
		//registerService
		try {
			bc.registerService(BundleActivator.class.getName(), this, null);
			_link.log("Were able to register service BundleActivator", "OK");
		}
		catch (Exception e) {
			_link.log("Exception in testBundleContextPermissions", "FAIL");
			e.printStackTrace();
		}
		try {
			bc.registerService(TestPermission.class.getName(), this, null);
			if (sm == null)
				_link
						.log(
								"Got security exception when regestering service without permission or no security manager present",
								"OK");
			else
				_link
						.log(
								"Were able to register service without having permissions",
								"FAIL");
		}
		catch (SecurityException se) {
			_link
					.log(
							"Got security exception when regestering service without permission or no security manager present",
							"OK");
		}
		catch (Exception e) {
			_link.log("Exception in testBundleContextPermissions", "FAIL");
			e.printStackTrace();
		}
		//getReference
		try {
			ServiceReference permissionServiceRef = bc
					.getServiceReference(PermissionAdmin.class.getName());
			if (permissionServiceRef != null) {
				if (sm == null)
					_link
							.log(
									"Unable to get service reference to a service to which we lacked permission or no security manager present",
									"OK");
				else
					_link
							.log(
									"Were able to get service refernce to permission admin without permission",
									"FAIL");
			}
			else {
				_link
						.log(
								"Unable to get service reference to a service to which we lacked permission or no security manager present",
								"OK");
			}
		}
		catch (Exception e) {
			_link.log("Exception in testBundleContextPermissions", "FAIL");
			e.printStackTrace();
		}
		//getService
		//We got the service refernce earlier when tb10 had allPermissions
		try {
			if (_permServiceRef != null) {
				bc.getService(_permServiceRef);
				if (sm == null)
					_link
							.log(
									"Got security exception when getting service without having permission or no security manager present",
									"OK");
				else
					_link
							.log(
									"Were able to get permission admin without permission",
									"FAIL");
			}
			else {
				if (sm == null)
					_link
							.log(
									"Got security exception when getting service without having permission or no security manager present",
									"OK");
				else
					_link
							.log(
									"Didn't have a service reference to permission admin",
									"FAIL");
			}
		}
		catch (SecurityException se) {
			_link
					.log(
							"Got security exception when getting service without having permission or no security manager present",
							"OK");
		}
		catch (Exception e) {
			_link.log("Exception in testBundleContextPermissions", "FAIL");
			e.printStackTrace();
		}
	}

	void quit() {
		if (_continue) {
			bc.ungetService(_linkRef);
			_linkRef = null;
			_continue = false;
		}
	}
}
