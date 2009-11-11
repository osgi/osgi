/*
 * Copyright 2008 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.impl.bundle.jmx.framework;

import static org.osgi.framework.Constants.OBJECTCLASS;

import java.io.IOException;
import java.util.ArrayList;

import javax.management.Notification;
import javax.management.openmbean.TabularData;

import org.osgi.framework.AllServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.bundle.jmx.Monitor;
import org.osgi.impl.bundle.jmx.codec.OSGiProperties;
import org.osgi.impl.bundle.jmx.framework.codec.OSGiService;
import org.osgi.impl.bundle.jmx.framework.codec.OSGiServiceEvent;
import org.osgi.jmx.framework.ServiceStateMBean;
import org.osgi.util.tracker.ServiceTracker;

/** 
 * 
 */
public class ServiceState extends Monitor implements ServiceStateMBean {
	public ServiceState(BundleContext bc) {
		this.bc = bc;
	}

	public long getBundleIdentifier(long serviceId) throws IOException {
		return ref(serviceId).getBundle().getBundleId();
	}

	public TabularData getProperties(long serviceId) throws IOException {
		return OSGiProperties.tableFrom(ref(serviceId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.ServiceStateMBean#getBundle(long)
	 */

	public String[] getObjectClass(long serviceId) throws IOException {
		return (String[]) ref(serviceId).getProperty(OBJECTCLASS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.ServiceStateMBean#listServices()
	 */

	public TabularData listServices() {
		ArrayList<OSGiService> services = new ArrayList<OSGiService>();
		for (Bundle bundle : bc.getBundles()) {
			ServiceReference[] refs = bundle.getRegisteredServices();
			if (refs != null) {
				for (ServiceReference ref : refs) {
					services.add(new OSGiService(ref));
				}
			}
		}
		return OSGiService.tableFrom(services);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.ServiceStateMBean#getServiceInterfaces(long)
	 */

	public long[] getUsingBundles(long serviceId) throws IOException {
		Bundle[] bundles = ref(serviceId).getUsingBundles();
		long[] ids = new long[bundles.length];
		for (int i = 0; i < bundles.length; i++) {
			ids[i] = bundles[i].getBundleId();
		}
		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.ServiceStateMBean#getServices()
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.Monitor#addListener()
	 */
	@Override
	protected void addListener() {
		serviceListener = getServiceListener();
		bc.addServiceListener(serviceListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.ServiceStateMBean#getUsingBundles(long)
	 */

	protected ServiceListener getServiceListener() {
		return new AllServiceListener() {
			public void serviceChanged(ServiceEvent serviceEvent) {
				Notification notification = new Notification(
						ServiceStateMBean.EVENT, objectName, sequenceNumber++);
				notification.setUserData(new OSGiServiceEvent(serviceEvent)
						.asCompositeData());
				sendNotification(notification);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.Monitor#removeListener()
	 */
	@Override
	protected void removeListener() {
		if (serviceListener != null) {
			bc.removeServiceListener(serviceListener);
		}
	}

	protected ServiceReference ref(long serviceId) throws IOException {
		Filter filter;
		try {
			filter = bc.createFilter("(" + Constants.SERVICE_ID + "="
					+ serviceId + ")");
		} catch (InvalidSyntaxException e) {
			throw new IOException("Invalid filter syntax: " + e);
		}
		ServiceTracker tracker = new ServiceTracker(bc, filter, null);
		tracker.open();
		ServiceReference serviceReference = tracker.getServiceReference();
		if (serviceReference == null) {
			throw new IllegalArgumentException("Service <" + serviceId
					+ "> does not exist");
		}
		tracker.close();
		return serviceReference;
	}

	protected ServiceListener serviceListener;
	protected BundleContext bc;

}
