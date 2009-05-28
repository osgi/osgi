/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.secure.lifecycle.tb5;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Bundle for the event test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class EventTest implements BundleActivator {
	ServiceRegistration	_sr;

	/**
	 * Starts the bundle. Registers a service.
	 */
	public void start(BundleContext bc) {
		Hashtable props;
		props = new Hashtable();
		props.put("Service-Name", "EventTest");
		_sr = bc.registerService(EventTest.class.getName(), this, props);
	}

	/**
	 * Stops the bundle. Unregisters the service.
	 */
	public void stop(BundleContext bc) {
		try {
			_sr.unregister();
		}
		catch (IllegalStateException e) { /* Ignore */
		}
	}
}
