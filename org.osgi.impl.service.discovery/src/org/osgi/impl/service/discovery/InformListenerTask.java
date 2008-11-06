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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Vector;

import org.osgi.framework.Filter;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServiceListener;

/**
 * A TimerTask that compares in its run method the registered filters with the
 * available services and informs the listeners if its filter matches.
 * 
 * @version 0.6
 * @author Thomas Kiesslich
 */
public class InformListenerTask extends TimerTask {
	private Discovery discovery;

	private Collection/* <ServiceEndpointDescription> */lastLookupResult = null;

	/**
	 * 
	 * @param listeners
	 *            the Map and ServiceListeners and its filter
	 * @param disco
	 *            the Instance of Discovery to get the published services from
	 */
	public InformListenerTask(Discovery disco) {
		discovery = disco;
	}

	/**
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		synchronized (AbstractDiscovery.getListenerAndFilter()) {
			if (AbstractDiscovery.getListenerAndFilter().size() != 0) {
				Collection/* <ServiceEndpointDescription> */descriptions = discovery
						.findService(null, null);
				Vector availableServices = new Vector();
				notifyAvailableServices(descriptions, availableServices);
				// notify all about unavailable services
				notifyUnavailableServices(availableServices);
				// now store the last find result for the next check
				lastLookupResult = descriptions;
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
			Iterator it = AbstractDiscovery.getListenerAndFilter().keySet()
					.iterator();
			while (it.hasNext()) {
				ServiceListener listener = (ServiceListener) it.next();
				notifiyAvailableServicePerListener(availableServices, descr,
						listener, (Filter) AbstractDiscovery
								.getListenerAndFilter().get(listener));
			}
		}
	}

	/**
	 * @param availableServices
	 */
	private void notifyUnavailableServices(Vector availableServices) {
		if (lastLookupResult != null) {
			Iterator llrIt = lastLookupResult.iterator();
			int i = 0;
			while (llrIt.hasNext()) {
				if (!availableServices.contains(new Integer(i))) {
					Iterator it = AbstractDiscovery.getListenerAndFilter()
							.keySet().iterator();
					while (it.hasNext()) {
						ServiceListener l = (ServiceListener) it.next();
						l.serviceUnavailable((ServiceEndpointDescription) llrIt
								.next());
					}
				}
				i++;
			}
		}
	}

	/**
	 * @param availableServices
	 * @param descr
	 * @param l
	 * @param f
	 */
	private void notifiyAvailableServicePerListener(Vector availableServices,
			ServiceEndpointDescription descr, ServiceListener l, Filter f) {
		// check if the listener filter matches the given
		// description properties. That prerequisites that all information of a
		// service description are in its properties bag
		if ((f == null) || (f.match(new Hashtable(descr.getProperties())))) {
			// check if this is the first run
			if (lastLookupResult != null && lastLookupResult.size() > 0) {
				// it's not
				Iterator it = lastLookupResult.iterator();
				while (it.hasNext()) {
					Integer index = null;
					ServiceEndpointDescription sed = (ServiceEndpointDescription) it
							.next();
					// look up the last result map to see if we already know
					// that description
					// TODO we currently have not idea if a service description
					// has changed or not. There is ID that identifies a service
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
						l.serviceModified(sed, descr);
					} else {
						// notify a listener that a service
						// description matches the specified filter
						// and
						// is new to him
						l.serviceAvailable(descr);
					}
				}
			} else {
				// We assume this is the first run. We notify listeners that the
				// matching service is available
				l.serviceAvailable(descr);
			}
		}
	}
}
