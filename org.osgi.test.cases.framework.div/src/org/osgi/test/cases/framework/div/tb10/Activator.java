/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.div.tb10;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The test bundle activator
 * 
 * @author Ericsson Radio Systems AB
 */
public class Activator implements BundleActivator {

	TestServiceImpl		service;
	ServiceRegistration	serviceRegistration;

	/**
	 * Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		service = new TestServiceImpl();
		serviceRegistration = bc
				.registerService(TestService.class.getName(), service, new Properties());
	}

	/**
	 * Stops the bundle by unregistering the services.
	 */
	public void stop(BundleContext bc) {
		try {
			serviceRegistration.unregister();
		}
		catch (IllegalStateException e) { /* Ignore */
		}
		
		service = null;
		serviceRegistration = null;
	}
}