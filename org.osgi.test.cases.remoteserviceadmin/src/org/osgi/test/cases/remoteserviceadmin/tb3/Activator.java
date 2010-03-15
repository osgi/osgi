/*
 * Copyright (c) 2008 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.remoteserviceadmin.tb3;

import junit.framework.Assert;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class Activator implements BundleActivator {
	BundleContext                  context;
	ServiceTracker tracker;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		test();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		teststop();
	}

	/**
	 * Searches for a proxy for A, which was created from an endpoint description
	 * added as file in a bundle.
	 * 
	 * @throws Exception
	 */
	public void test() throws Exception {
		Filter filter = context.createFilter("(&(objectClass=" + A.class.getName() +")(newkey=newvalue))");
		tracker = new ServiceTracker(context, filter, null);
		tracker.open();
		
		A service = (A) tracker.waitForService(60000);
		Assert.assertNotNull("no service A found", service);
		
		// call the service
		Assert.assertEquals("A", service.getA());

		tracker.close();
	}

	/**
	 * Make sure the service goes away when the endpoint description is removed.
	 * 
	 * @throws Exception
	 */
	private void teststop() throws Exception {
		if (tracker == null) {
			return;
		}
		
		tracker.open();
		
		Assert.assertNull("service A found, but was expected to be gone", tracker.waitForService(60000));
	}
}
