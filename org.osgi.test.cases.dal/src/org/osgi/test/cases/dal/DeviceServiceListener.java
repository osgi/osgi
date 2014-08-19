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
final class DeviceServiceListener implements ServiceListener {

	private static final String	DEVICE_FILTER	=
														"(|(" + Constants.OBJECTCLASS + '=' + Device.class.getName() + ")(" +
																Constants.OBJECTCLASS + '=' + Function.class.getName() + "))";

	private final List			events;
	private final BundleContext	bc;

	public DeviceServiceListener(BundleContext bc) {
		this.bc = bc;
		this.events = new ArrayList();
	}

	public void register() throws InvalidSyntaxException {
		this.bc.addServiceListener(this, DEVICE_FILTER);
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
			return (ServiceEvent[]) this.events.toArray(new ServiceEvent[this.events.size()]);
		}
	}

	public void clear() {
		synchronized (this.events) {
			this.events.clear();
		}
	}

}
