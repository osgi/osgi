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
import java.util.Map;
import java.util.TimerTask;

import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.service.discovery.ServiceEndpointDescription;

/**
 * A TimerTask that compares in its run method the registered filters with the
 * available services and informs the listeners if its filter matches.
 * 
 * @version 0.6
 * @author Thomas Kiesslich
 */
public class InformListenerTask extends TimerTask {
	private SLPHandlerImpl discovery;

	private Collection/* <ServiceEndpointDescription> */lastLookupResult = new ArrayList();

	/**
	 * 
	 * @param listeners
	 *            the Map and ServiceListeners and its filter
	 * @param disco
	 *            the Instance of Discovery to get the published services from
	 */
	public InformListenerTask(SLPHandlerImpl disco) {
		discovery = disco;
	}

	/**
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (discovery.getRegisteredServiceTracker().size() != 0) {
			Collection allAvailableServices = discovery.findService(null, null);
			notifyAvailableServices(allAvailableServices);
			
			// notify all about vanished services
			Collection vanishedServices = new HashSet(lastLookupResult);
			vanishedServices.removeAll(allAvailableServices);			
			notifyUnavailableServices(vanishedServices);
			// now store the last find result for the next check
			synchronized (lastLookupResult) {
				lastLookupResult = new ArrayList(allAvailableServices);
			}
		}
	}

	/**
	 * @param descriptions
	 * @param availableServices
	 */
	private void notifyAvailableServices(Collection/* <ServiceEndpointDescription> */ descriptions) {
		Iterator descrIt = descriptions.iterator();
		while (descrIt.hasNext()) {
			ServiceEndpointDescription descr = (ServiceEndpointDescription) descrIt
					.next();
			// walk over the registered service tracker
			Map trackers = discovery.getRegisteredServiceTracker();
			synchronized (trackers) {
				Iterator it = trackers.keySet().iterator();
				while (it.hasNext()) {
					DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) it
							.next();
					notifyAvailableServicePerListener(descr, tracker, (Map) trackers.get(tracker));
				}
			}
		}
	}

	/**
	 * @param availableServices
	 */
	private void notifyUnavailableServices(Collection vanishedServices) {
		Iterator svcDescrIt = vanishedServices.iterator();
		while (svcDescrIt.hasNext()) {
			ServiceEndpointDescription sed = (ServiceEndpointDescription) svcDescrIt.next();

			Map trackers = discovery.getRegisteredServiceTracker();
			synchronized (trackers) {
				Iterator trackerIt = trackers.keySet().iterator();
				while (trackerIt.hasNext()) {
					DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) trackerIt
							.next();
					if (SLPHandlerImpl.isTrackerInterestedInSED(sed,
							(Map) trackers.get(tracker))) {
						tracker.serviceChanged(new DiscoveredServiceNotificationImpl(
										sed,
										DiscoveredServiceNotification.UNAVAILABLE));
					}
				}
			}
		}
	}

	/**
	 * TODO: Verify that notification logic doesn't repeat notifications to
	 * trackers
	 * 
	 * @param availableServices
	 * @param svcDescr
	 * @param tracker
	 * @param trackerProps
	 */
	private void notifyAvailableServicePerListener(ServiceEndpointDescription svcDescr, DiscoveredServiceTracker tracker,
			Map trackerProps) {
		// check if the listener filter matches the given
		// description properties. That prerequisites that all information of a
		// service description are in its properties bag
		if (SLPHandlerImpl.isTrackerInterestedInSED(svcDescr, trackerProps)) {
			if (lastLookupResult != null) {
				synchronized (lastLookupResult) {
					// if we already know that service
					if(lastLookupResult.contains(svcDescr))
					{
						// then check whether it has been modified
						// TODO we currently have not idea if a service
						// description
						// has changed or not. There is ID that identifies a
						// service
						// description. Should that ID part of the spec??
						boolean modified = false;
						if(modified)
						{
							tracker.serviceChanged(new DiscoveredServiceNotificationImpl(
								svcDescr,
								DiscoveredServiceNotification.MODIFIED));
						}
					}
					else{
						// notify listener that a service description matches the specified filter
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
}
