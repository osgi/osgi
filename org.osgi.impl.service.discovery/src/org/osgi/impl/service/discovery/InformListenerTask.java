/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.impl.service.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.Vector;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.log.LogService;

/**
 * A TimerTask that compares in its run method the registered filters with the
 * available services and informs the listeners if its filter matches.
 * 
 * @version 0.6
 * @author Thomas Kiesslich
 */
public class InformListenerTask extends TimerTask {
	private AbstractDiscovery discovery;

	private Collection/* <ServiceEndpointDescription> */lastLookupResult = new ArrayList();

	/**
	 * 
	 * @param listeners
	 *            the Map and ServiceListeners and its filter
	 * @param disco
	 *            the Instance of Discovery to get the published services from
	 */
	public InformListenerTask(AbstractDiscovery disco) {
		discovery = disco;
	}

	/**
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (discovery.getRegisteredServiceTracker().size() != 0) {
			Collection descriptions = null;
			try {
				descriptions = discovery.findService(null, null);
			} catch (InvalidSyntaxException e) {
				discovery.getLogService().log(LogService.LOG_ERROR,
						"findService threw an exception ", e);
			}
			// TODO: do we really need that Vector availableServices? why not
			// just take "descriptions"
			Vector availableServices = new Vector();
			notifyAvailableServices(descriptions, availableServices);
			// notify all about unavailable services
			notifyUnavailableServices(availableServices);
			// now store the last find result for the next check
			synchronized (lastLookupResult) {
				lastLookupResult = new ArrayList(descriptions);
			}
		}
	}

	/**
	 * @param descriptions
	 * @param availableServices
	 */
	private void notifyAvailableServices(
			Collection/* <ServiceEndpointDescription> */descriptions,
			Vector availableServices) {
		Iterator descrIt = descriptions.iterator();
		while (descrIt.hasNext()) {
			ServiceEndpointDescription descr = (ServiceEndpointDescription) descrIt
					.next();
			// walk over the registered listeners
			Map tracker = discovery.getRegisteredServiceTracker();
			Iterator it = tracker.keySet().iterator();
			while (it.hasNext()) {
				DiscoveredServiceTracker listener = (DiscoveredServiceTracker) it
						.next();
				notifiyAvailableServicePerListener(availableServices, descr,
						listener, (Map) tracker.get(listener));
			}
		}
	}

	/**
	 * @param availableServices
	 */
	private void notifyUnavailableServices(Vector availableServices) {
		if (lastLookupResult != null) {
			synchronized (lastLookupResult) {
				Iterator llrIt = lastLookupResult.iterator();
				int i = 0;
				while (llrIt.hasNext()) {
					if (!availableServices.contains(new Integer(i))) {
						Iterator it = discovery.getRegisteredServiceTracker()
								.keySet().iterator();
						while (it.hasNext()) {
							DiscoveredServiceTracker l = (DiscoveredServiceTracker) it
									.next();
							l
									.serviceChanged(new DiscoveredServiceNotificationImpl(
											(ServiceEndpointDescription) llrIt
													.next(),
											DiscoveredServiceNotification.UNAVAILABLE));
						}
					}
					i++;
				}
			}
		}
	}

	/**
	 * TODO: Verify that notification logic doesn't repeat notifications to
	 * trackers
	 * 
	 * @param availableServices
	 * @param descr
	 * @param l
	 * @param props
	 */
	private void notifiyAvailableServicePerListener(Vector availableServices,
			ServiceEndpointDescription descr, DiscoveredServiceTracker l,
			Map props) {
		// check if the listener filter matches the given
		// description properties. That prerequisites that all information of a
		// service description are in its properties bag
		boolean matches = discovery.checkMatch(descr, props);
		if (matches) {
			// check if this is the first run
			if (lastLookupResult != null && lastLookupResult.size() > 0) {
				synchronized (lastLookupResult) {
					// it's not
					Iterator it = lastLookupResult.iterator();
					while (it.hasNext()) {
						Integer index = null;
						ServiceEndpointDescription sed = (ServiceEndpointDescription) it
								.next();
						// look up the last result map to see if we already know
						// that description
						// TODO we currently have not idea if a service
						// description
						// has changed or not. There is ID that identifies a
						// service
						// description. Should that ID part of the spec??
						if (sed.equals(descr)) {
							index = new Integer(0);
							availableServices.add(sed);
						}

						if (index != null) {
							// notify a listener that a service
							// description matches the specified filter
							// and
							// does exist before
							l
									.serviceChanged(new DiscoveredServiceNotificationImpl(
											descr,
											DiscoveredServiceNotification.MODIFIED));
						} else {
							// notify a listener that a service
							// description matches the specified filter
							// and
							// is new to him
							l
									.serviceChanged(new DiscoveredServiceNotificationImpl(
											descr,
											DiscoveredServiceNotification.AVAILABLE));
						}
					}
				}
			} else {
				// We assume this is the first run. We notify listeners that the
				// matching service is available
				l.serviceChanged(new DiscoveredServiceNotificationImpl(descr,
						DiscoveredServiceNotification.AVAILABLE));
			}
		}
	}
}
