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

package org.osgi.impl.service.discovery.slp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;

/**
 * A TimerTask that compares in its run method the registered filters with the
 * available services and informs the listeners if its filter matches.
 * 
 * @version 0.6
 * @author Thomas Kiesslich
 */
public class InformListenerTask extends TimerTask {
	private SLPHandlerImpl	discovery;

	/**
	 * 
	 * @param listeners the Map and ServiceListeners and its filter
	 * @param disco the Instance of Discovery to get the published services from
	 */
	public InformListenerTask(SLPHandlerImpl disco) {
		discovery = disco;
	}

	/**
	 * TODO: only run InformListenerTask if there's an existing
	 * DiscoveredTracker with non-null interfaces and filters props. Otherwise
	 * for example on the server-side, Discovery ends up rediscovering the local
	 * service that it's just advertized.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Map registeredServiceTracker = discovery.getRegisteredServiceTracker();
		if (registeredServiceTracker.size() != 0) {
			Collection /* <ServiceEndpointDescription> */lastLookupResult = discovery
					.getCachedServices();
			Collection allAvailableServices = discovery.findService(null, null);
			notifyAvailableServices(allAvailableServices, lastLookupResult,
					registeredServiceTracker);
			// notify all about vanished services
			Collection vanishedServices = new HashSet(lastLookupResult);
			vanishedServices.removeAll(allAvailableServices);
			notifyUnavailableServices(vanishedServices,
					registeredServiceTracker);
		}
	}

	/**
	 * @param descriptions
	 * @param availableServices
	 */
	private void notifyAvailableServices(Collection/*
													 * <ServiceEndpointDescription
													 * >
													 */descriptions,
			Collection lastLookupResult, Map trackers) {
		Iterator descrIt = descriptions.iterator();
		while (descrIt.hasNext()) {
			ServiceEndpointDescription descr = (ServiceEndpointDescription) descrIt
					.next();
			// walk over the registered service tracker
			Iterator it = trackers.keySet().iterator();
			while (it.hasNext()) {
				DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) it
						.next();
				notifyAvailableServicePerListener(descr, tracker,
						(Map) trackers.get(tracker), lastLookupResult);
			}
		}
	}

	/**
	 * @param availableServices
	 */
	private void notifyUnavailableServices(Collection vanishedServices,
			Map trackers) {
		Iterator svcDescrIt = vanishedServices.iterator();
		while (svcDescrIt.hasNext()) {
			ServiceEndpointDescription sed = (ServiceEndpointDescription) svcDescrIt
					.next();

			Iterator trackerIt = trackers.keySet().iterator();
			while (trackerIt.hasNext()) {
				DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) trackerIt
						.next();
				if (SLPHandlerImpl.isTrackerInterestedInSED(sed, (Map) trackers
						.get(tracker))) {
					tracker
							.serviceChanged(new DiscoveredServiceNotificationImpl(
									sed,
									DiscoveredServiceNotification.UNAVAILABLE));
				}
			}
		}
	}

	/**
	 * Compares two instances of ServiceEndpointDescription and evaluates if
	 * something is different.
	 * 
	 * @param oldDescr
	 * @param newDescr
	 * @return true if something is different, else false
	 */
	private boolean hasSvcDescrChanged(ServiceEndpointDescription oldDescr,
			ServiceEndpointDescription newDescr) {
		List sdJavaInterfaceAndVersions = new ArrayList();
		List sdJavaAndEndpointInterfaces = new ArrayList();
		Collection interfaces = newDescr.getProvidedInterfaces();
		if (interfaces == null) {
			throw new RuntimeException(
					"The service does not contain requiered parameter interfaces. "
							+ newDescr);
		}
		Iterator interfacesIterator = interfaces.iterator();
		while (interfacesIterator.hasNext()) {
			String interfaceName = (String) interfacesIterator.next();
			sdJavaInterfaceAndVersions.add(interfaceName
					+ ServicePublication.SEPARATOR
					+ newDescr.getVersion(interfaceName));
			if (newDescr.getEndpointInterfaceName(interfaceName) != null) {
				sdJavaAndEndpointInterfaces.add(interfaceName
						+ ServicePublication.SEPARATOR
						+ newDescr.getEndpointInterfaceName(interfaceName));
			}
		}

		Collection oldinterfaces = newDescr.getProvidedInterfaces();
		if (oldinterfaces == null) {
			throw new RuntimeException(
					"The service does not contain requiered parameter interfaces. "
							+ newDescr);
		}
		Iterator oldinterfacesIterator = oldinterfaces.iterator();
		Collection javaInterfaceAndVersions = new ArrayList();
		Collection javaAndEndpointInterfaces = new ArrayList();
		while (oldinterfacesIterator.hasNext()) {
			String interfaceName = (String) oldinterfacesIterator.next();
			javaInterfaceAndVersions.add(interfaceName
					+ ServicePublication.SEPARATOR
					+ newDescr.getVersion(interfaceName));
			if (newDescr.getEndpointInterfaceName(interfaceName) != null) {
				javaAndEndpointInterfaces.add(interfaceName
						+ ServicePublication.SEPARATOR
						+ newDescr.getEndpointInterfaceName(interfaceName));
			}
		}
		// interface and versions field
		if (!((javaInterfaceAndVersions == sdJavaInterfaceAndVersions) || (javaInterfaceAndVersions != null && javaInterfaceAndVersions
				.equals(sdJavaInterfaceAndVersions)))) {
			return false;
		}
		// interface and endpoints field
		if (!((javaAndEndpointInterfaces == sdJavaAndEndpointInterfaces) || (javaAndEndpointInterfaces != null && javaAndEndpointInterfaces
				.equals(sdJavaAndEndpointInterfaces)))) {
			return false;
		}
		Map properties = newDescr.getProperties();
		// properties field
		if (properties != oldDescr.getProperties()) {
			if (properties != null && oldDescr.getProperties() != null) {
				if (properties.isEmpty() && !oldDescr.getProperties().isEmpty()) {
					return false;
				}
				Iterator it = properties.keySet().iterator();
				while (it.hasNext()) {
					String nextKey = (String) it.next();
					if (oldDescr.getProperty(nextKey) != null) {
						if (properties.get(nextKey).equals(
								oldDescr.getProperty(nextKey))) {

						}
						else {
							return false;
						}
					}
					else {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param availableServices
	 * @param svcDescr
	 * @param tracker
	 * @param trackerProps
	 */
	private void notifyAvailableServicePerListener(
			ServiceEndpointDescription svcDescr,
			DiscoveredServiceTracker tracker, Map trackerProps,
			Collection lastLookupResult) {
		// check if the listener filter matches the given
		// description properties. That prerequisites that all information of a
		// service description are in its properties bag
		if (SLPHandlerImpl.isTrackerInterestedInSED(svcDescr, trackerProps)) {
			if (lastLookupResult != null) {
				// if we already know that service
				if (lastLookupResult.contains(svcDescr)) {
					// check whether it has been modified
					ServiceEndpointDescription oldDescr = null;
					boolean modified = false;
					Iterator it = lastLookupResult.iterator();
					while (it.hasNext()) {
						oldDescr = (ServiceEndpointDescription) it.next();
						if (oldDescr.equals(svcDescr)) {
							modified = hasSvcDescrChanged(oldDescr, svcDescr);
						}
					}
					if (modified) {
						tracker
								.serviceChanged(new DiscoveredServiceNotificationImpl(
										svcDescr,
										DiscoveredServiceNotification.MODIFIED));
					}
				}
				else {
					// notify listener that a service description matches
					// the specified filter
					// and is new to him
					tracker
							.serviceChanged(new DiscoveredServiceNotificationImpl(
									svcDescr,
									DiscoveredServiceNotification.AVAILABLE));
				}

			}
		}
	}
}
