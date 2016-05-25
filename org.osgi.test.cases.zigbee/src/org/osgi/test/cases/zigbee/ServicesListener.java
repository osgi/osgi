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

package org.osgi.test.cases.zigbee;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
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
public class ServicesListener extends ServiceTracker {

	private final Semaphore	waiter;
	private final int		desiredCount;
	private ZigBeeNode		last;
	private ArrayList		nodeList	= new ArrayList();
	private int				size;

	/**
	 * @param bc
	 * @param count
	 * @throws InvalidSyntaxException
	 */
	public ServicesListener(BundleContext bc, int count) throws InvalidSyntaxException {
		super(bc, bc.createFilter("(&(objectclass=org.osgi.service.zigbee.ZigBeeNode))"), null);
		waiter = new Semaphore();
		desiredCount = count;
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
		ZigBeeNode device = (ZigBeeNode) super.addingService(ref);

		DefaultTestBundleControl.log("adding ZigBee Node " + device);

		synchronized (this) {
			size++;
			nodeList.add(device);
			last = device;
			if (size != desiredCount) {
				return device;
			}
		}
		DefaultTestBundleControl.log(desiredCount + " ZigBee Devices arrived, signaling waiter");
		waiter.signal();
		return device;
	}

	public void removedService(ServiceReference reference, Object service) {
		DefaultTestBundleControl.log("removing ZigBee Node " + service);
		super.removedService(reference, service);
		synchronized (this) {
			size--;
		}
	}

	/**
	 * @return the "last seen" ZigBeeNode.
	 */
	public synchronized ZigBeeNode getLastZigBeeNode() {
		return last;
	}

	public synchronized ZigBeeNode getZigBeeNode(BigInteger IEEEAddress) {
		ZigBeeNode result = null;

		for (Iterator it = nodeList.iterator(); it.hasNext();) {
			ZigBeeNode node = (ZigBeeNode) it.next();

			if (node.getIEEEAddress().equals(IEEEAddress)) {
				result = node;
			}
		}

		return result;
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
		DefaultTestBundleControl.log("waiting for ZigBee Nodes " + timeout);
		waiter.waitForSignal(timeout);
	}

}
