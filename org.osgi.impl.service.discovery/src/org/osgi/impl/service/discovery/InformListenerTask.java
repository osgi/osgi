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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
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
	private Map listeners;
	private Discovery discovery;

	private ServiceEndpointDescription[] lastLookupResult = null;

	/**
	 * 
	 * @param listeners
	 *            the Map and ServiceListeners and its filter
	 * @param disco
	 *            the Instance of Discovery to get the published services from
	 */
	public InformListenerTask(Map serviceListeners, Discovery disco) {
		listeners = serviceListeners;
		discovery = disco;
	}

	/**
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		synchronized (listeners) {
			if (listeners.size() != 0) {
				ServiceEndpointDescription[] descriptions = discovery
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
			ServiceEndpointDescription[] descriptions, Vector availableServices) {
		for (int i = 0; i < descriptions.length; i++) {
			ServiceEndpointDescription descr = descriptions[i];
			// walk over the registered listeners
			Iterator it = listeners.keySet().iterator();
			while (it.hasNext()) {
				ServiceListener listener = (ServiceListener) it.next();
				notifiyAvailableServicePerListener(availableServices, descr,
						listener, (Filter) listeners.get(listener));
			}
		}
	}

	/**
	 * @param availableServices
	 */
	private void notifyUnavailableServices(Vector availableServices) {
		if (lastLookupResult != null) {
			for (int i = 0; i < lastLookupResult.length; i++) {
				if (!availableServices.contains(new Integer(i))) {
					Iterator it = listeners.keySet().iterator();
					while (it.hasNext()) {
						ServiceListener l = (ServiceListener) it.next();
						l.serviceUnavailable(lastLookupResult[i]);
					}
				}
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
			if (lastLookupResult != null) {
				// it's not
				Integer index = null;
				for (int j = 0; j < lastLookupResult.length; j++) {
					// look up the last result map to see if we already know
					// that description
					// TODO we currently have not idea if a service description
					// has changed or not. There is ID that identifies a service
					// description. Should that ID part of the spec??
					if (lastLookupResult[j].equals(descr)) {
						index = new Integer(j);
						availableServices.add(index);
					}
					if (index != null) {
						// notify a listener that a service
						// description matches the specified filter
						// and
						// does exist before
						l.serviceModified(lastLookupResult[j], descr);
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
