/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.zigbee.util.teststep;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.test.support.compatibility.Semaphore;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Seems to track the latest registration/modification/unregistration of a
 * device. It was using a service listener with all dangers associated. It also
 * did not discriminate between the looked for devices and the existing or found
 * devices on the network.
 * 
 * @author $Id$
 */
public class EndpointServicesListener extends ServiceTracker {

	private final Semaphore	waiter;
	private ZigBeeEndpoint	last;
	private ArrayList		endpointList	= new ArrayList();
	private int				size;
	private BundleContext	bc;

	/**
	 * @param bc
	 * @throws InvalidSyntaxException
	 */

	public EndpointServicesListener(BundleContext bc) throws InvalidSyntaxException {
		super(bc, bc.createFilter("(&(objectclass=org.osgi.service.zigbee.ZigBeeEndpoint))"), null);
		waiter = new Semaphore();
		this.bc = bc;

		size = 0;
	}

	public void open() {
		super.open();
		synchronized (this) {
			size = super.size();
		}
	}

	public void open(boolean trackAllServices) {
		super.open(trackAllServices);
		synchronized (this) {
			size = super.size();
		}
	}

	public void close() {
		super.close();
		synchronized (this) {
			size = super.size();
		}
	}

	public synchronized int size() {
		return size;
	}

	public Object addingService(ServiceReference ref) {
		ZigBeeEndpoint device = (ZigBeeEndpoint) super.addingService(ref);

		synchronized (this) {
			size++;
			boolean isOk = checkId(device);
			if (isOk) {
				endpointList.add(device);
				ZigBeeNode node = getZigBeeNode(device.getNodeAddress());
				node.getEndpoints();
			}
			last = device;

			return device;
		}

	}

	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
		synchronized (this) {
			size--;
		}
	}

	/**
	 * @return the "last seen" ZigBeeNode.
	 */
	public synchronized ZigBeeEndpoint getLastZigBeeNode() {
		return last;
	}

	public synchronized List getEndpointList() {
		return endpointList;
	}

	public synchronized ZigBeeEndpoint getZigBeeEndpoint(BigInteger IEEEAddress) {
		ZigBeeEndpoint result = null;

		for (Iterator it = endpointList.iterator(); it.hasNext();) {
			ZigBeeEndpoint endpoint = (ZigBeeEndpoint) it.next();

			if (endpoint.getNodeAddress().equals(IEEEAddress)) {
				result = endpoint;
			}
		}

		return result;
	}

	private boolean checkId(ZigBeeEndpoint ep) {
		boolean ok = true;
		for (Iterator it = endpointList.iterator(); it.hasNext();) {
			ZigBeeEndpoint endpoint = (ZigBeeEndpoint) it.next();

			if (endpoint.getId() == ep.getId() && endpoint.getNodeAddress().equals(ep.getNodeAddress())) {
				ok = false;
				ep.notExported(new ZigBeeException(ZigBeeException.OSGI_EXISTING_ID, "this Id already exists"));

			}
		}
		return ok;
	}

	private ZigBeeNode getZigBeeNode(BigInteger nodeIeeeAddress) {
		ZigBeeNode node = null;
		try {
			ServiceReference[] srs = bc.getAllServiceReferences(ZigBeeNode.class.getName(), null);
			int srsIndex = 0;
			while (srsIndex < srs.length) {
				ServiceReference sr = srs[srsIndex];

				int j = 0;
				if (nodeIeeeAddress.equals(sr.getProperty(ZigBeeNode.IEEE_ADDRESS))) {
					// The sr's value associated to ZigBeeEndpoint.PROFILE_ID
					// may also be checked.
					node = (ZigBeeNode) bc.getService(sr);
					break;
				}
				srsIndex = srsIndex + 1;
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();

		}
		return node;
	}

	/**
	 * Waits until a signal is (or has been) given (see
	 * org.osgi.test.support.compatibility.Semaphore).
	 * 
	 * @param timeout The maximum number of milliseconds to wait for a signal or
	 *        0 to wait indefinitely.
	 * 
	 * @throws InterruptedException
	 */
	public void waitFor(long timeout) throws InterruptedException {
		waiter.waitForSignal(timeout);
	}

}
