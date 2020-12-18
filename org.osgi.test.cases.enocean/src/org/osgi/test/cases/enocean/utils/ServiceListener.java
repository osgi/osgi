/*
 * Copyright (c) OSGi Alliance (2014, 2020). All Rights Reserved.
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

package org.osgi.test.cases.enocean.utils;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author $Id$
 */
public class ServiceListener<T> extends ServiceTracker<T,T> {

	@SuppressWarnings("unused")
	static private final String TAG = "ServiceListener";

	/** SERVICE_ADDED */
	public static final String	SERVICE_ADDED		= "SERVICE_ADDED";
	/** SERVICE_MODIFIED */
	public static final String	SERVICE_MODIFIED	= "SERVICE_MODIFIED";
	/** SERVICE_REMOVED */
	public static final String	SERVICE_REMOVED		= "SERVICE_REMOVED";

	private final Semaphore		waiter;

	private BundleContext		bc;
	private LinkedList<String>	lastActions;
	private ServiceReference<T>	serviceReference;

	/**
	 * @param bc
	 * @param cls
	 * @throws InvalidSyntaxException
	 */
	public ServiceListener(BundleContext bc, Class<T> cls)
			throws InvalidSyntaxException {
		super(bc, cls, null);
		this.bc = bc;
		this.lastActions = new LinkedList<>();
		waiter = new Semaphore(0);
		open();
	}

	public T addingService(ServiceReference<T> ref) {
		T service = bc.getService(ref);
		if (service != null) {
			serviceReference = ref;
			lastActions.addFirst(SERVICE_ADDED);
			waiter.release();
		}
		return service;
	}

	public void modifiedService(ServiceReference<T> ref, T service) {
		super.modifiedService(ref, service);
		if (service != null) {
			serviceReference = ref;
			lastActions.addFirst(SERVICE_MODIFIED);
			waiter.release();
		}
	}

	public void removedService(ServiceReference<T> ref, T service) {
		super.removedService(ref, service);
		if (service != null) {
			serviceReference = ref;
			lastActions.addFirst(SERVICE_REMOVED);
			waiter.release();
		}
	}

	/**
	 * 
	 * @return the latest service reference that had known an event.
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#getServiceReference()
	 */
	public synchronized ServiceReference<T> getServiceReference() {
		return serviceReference;
	}

	/**
	 * Waits for an event to occur.
	 * 
	 * @param timeout
	 * @return the event type (added, modified, removed) or null.
	 * @throws InterruptedException
	 */
	public String waitForEvent(long timeout) throws InterruptedException {
		if (!lastActions.isEmpty()) {
			return lastActions.removeLast();
		}
		if (waiter.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
			return lastActions.isEmpty() ? null : (String) lastActions
					.removeLast();
		}
		return null;
	}

	/**
	 * @return the event type (added, modified, removed) or null.
	 * @throws InterruptedException
	 */
	public String waitForService() throws InterruptedException {
		return waitForEvent(OSGiTestCaseProperties.getTimeout());
	}

	public void close() {
		if (serviceReference != null) {
			serviceReference = null;
		}
		lastActions.clear();
		super.close();
	}

}
