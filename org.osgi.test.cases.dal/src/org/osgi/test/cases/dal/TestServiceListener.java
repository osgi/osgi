/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;

/**
 * Test listener to track device and function related service events.
 */
final class TestServiceListener implements ServiceListener {

	public static final String	DEVICE_FILTER			= '(' + Constants.OBJECTCLASS + '=' + Device.class.getName() + ')';

	public static final String	DEVICE_FUNCTION_FILTER	= "(|" + DEVICE_FILTER +
																'(' + Constants.OBJECTCLASS + '=' + Function.class.getName() + "))";

	private final List<ServiceEvent>	events;
	private final BundleContext	bc;

	public TestServiceListener(BundleContext bc, String filter) throws InvalidSyntaxException {
		this.bc = bc;
		this.events = new ArrayList<>();
		this.bc.addServiceListener(this, filter);
	}

	public void unregister() {
		this.bc.removeServiceListener(this);
	}

	public void serviceChanged(ServiceEvent event) {
		synchronized (this.events) {
			this.events.add(event);
		}
	}

	public ServiceEvent[] getEvents() {
		synchronized (this.events) {
			return this.events.toArray(new ServiceEvent[0]);
		}
	}

	public void clear() {
		synchronized (this.events) {
			this.events.clear();
		}
	}
}
