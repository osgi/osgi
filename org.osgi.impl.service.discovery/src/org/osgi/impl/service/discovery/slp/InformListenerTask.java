/* 
 * Copyright (c) 2008, 2009 Siemens Enterprise Communications GmbH & Co. KG, 
 * Germany. All rights reserved.
 *
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Siemens Enterprise Communications 
 * GmbH & Co. KG and its licensors. All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 */
package org.osgi.impl.service.discovery.slp;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

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
	 * service that it's just advertised.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Map registeredServiceTracker = discovery.getRegisteredServiceTracker();
		if (registeredServiceTracker.size() > 0) {
			Map /* <ServiceEndpointDescription> */lastLookupResult = discovery
					.getCachedServices();
			Map allAvailableServices = discovery.findService(null, null);
			notifyAvailableServices(allAvailableServices, lastLookupResult,
					registeredServiceTracker);
			// notify all about vanished services
			Iterator it = allAvailableServices.keySet().iterator();
			while(it.hasNext()){
				lastLookupResult.remove(it.next());
			}
			notifyUnavailableServices(lastLookupResult,
					registeredServiceTracker);
		}
	}

	/**
	 * @param descriptions
	 * @param availableServices
	 */
	private void notifyAvailableServices(Map/*
													 * <ServiceEndpointDescription
													 * >
													 */descriptions,
			Map lastLookupResult, Map trackers) {
		Iterator descrIt = descriptions.values().iterator();
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
	private void notifyUnavailableServices(Map vanishedServices,
			Map trackers) {
		Iterator svcDescrIt = vanishedServices.values().iterator();
		while (svcDescrIt.hasNext()) {
			ServiceEndpointDescription sed = (ServiceEndpointDescription) svcDescrIt
					.next();

			Iterator trackerIt = trackers.keySet().iterator();
			while (trackerIt.hasNext()) {
				DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) trackerIt
						.next();
				if (SLPHandlerImpl.isTrackerInterestedInSED(sed, (Map) trackers
						.get(tracker))) {
					SLPHandlerImpl.log(LogService.LOG_INFO, this.getClass()
							.getName()
							+ ": Notify "
							+ tracker
							+ " about the GONE service " + sed);
					tracker
							.serviceChanged(new DiscoveredServiceNotificationImpl(
									sed,
									DiscoveredServiceNotification.UNAVAILABLE));
				}
			}
		}
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
			Map lastLookupResult) {
		// check if the listener filter matches the given
		// description properties. That prerequisites that all information of a
		// service description are in its properties bag
		if (SLPHandlerImpl.isTrackerInterestedInSED(svcDescr, trackerProps)) {
			if (lastLookupResult != null) {
				// if we already know that service
				if (lastLookupResult.containsKey(svcDescr.getEndpointID())) {
					// check whether it has been modified
					//TODO: this does not work because the endpoint ids are identical
					if (!((ServiceEndpointDescription) lastLookupResult
							.get(svcDescr.getEndpointID())).equals(svcDescr)) {
						//if yes
						SLPHandlerImpl.log(LogService.LOG_INFO, this.getClass()
								.getName()
								+ ": Notify "
								+ tracker
								+ " about the MODIFIED service " + svcDescr);
						tracker
								.serviceChanged(new DiscoveredServiceNotificationImpl(
										svcDescr,
										DiscoveredServiceNotification.MODIFIED));
					}
				}
				else {
					// notify listener that a service description matches
					// the specified filter
					// and is new to it
					SLPHandlerImpl.log(LogService.LOG_INFO, this.getClass()
							.getName()
							+ ": Notify "
							+ tracker
							+ " about the NEW service "
							+ svcDescr);
					tracker
							.serviceChanged(new DiscoveredServiceNotificationImpl(
									svcDescr,
									DiscoveredServiceNotification.AVAILABLE));
				}

			}
		}
	}
}
