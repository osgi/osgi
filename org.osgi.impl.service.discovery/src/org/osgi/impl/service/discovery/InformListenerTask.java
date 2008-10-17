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
 * @version 0.5
 * @author Thomas Kiesslich
 */
public class InformListenerTask extends TimerTask {
	private Map listener;
	private Discovery discovery;

	private ServiceEndpointDescription[] lastLookupResult = new ServiceEndpointDescription[0];

	/**
	 * 
	 * @param listeners
	 *            the Map and ServiceListeners and its filter
	 * @param disco
	 *            the Instance of Discovery to get the published services from
	 */
	public InformListenerTask(Map listeners, Discovery disco) {
		listener = listeners;
		discovery = disco;
	}

	/**
	 * TODO this code looks crap
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		synchronized (listener) {
			if (listener.size() != 0) {
				ServiceEndpointDescription[] descriptions = discovery
						.findService(null, null);
				Vector availableServices = new Vector();
				for (int i = 0; i < descriptions.length; i++) {
					ServiceEndpointDescription descr = descriptions[i];
					Hashtable props = new Hashtable(descr.getProperties());
					// walk over the registered listeners
					Iterator it = listener.keySet().iterator();
					while (it.hasNext()) {
						ServiceListener l = (ServiceListener) it.next();
						Filter f = (Filter) listener.get(l);
						// check if the listeners filter matches the given
						// description properties
						if (f.match(props)) {
							ServiceEndpointDescription existingDescr = null;
							boolean exists = false;
							// check if this a new service description or not
							for (int j = 0; j < lastLookupResult.length; j++) {
								if (lastLookupResult[j].equals(descr)) {
									exists = true;
									existingDescr = descr;
									availableServices.add(new Integer(j));
								}
								if (exists) {
									// notify one listener that a service
									// description matches the specified filter
									// and
									// does exist before
									l.serviceModified(existingDescr, descr);
								} else {
									// notify one listener that a service
									// description matches the specified filter
									// and
									// is new to him
									l.serviceAvailable(descr);
								}
							}
						}
					}
				}
				// notify all about unavailable services
				for (int i = 0; i < lastLookupResult.length; i++) {
					if (!availableServices.contains(new Integer(i))) {
						Iterator it = listener.keySet().iterator();
						while (it.hasNext()) {
							ServiceListener l = (ServiceListener) it.next();
							l.serviceUnavailable(lastLookupResult[i]);
						}
					}
				}
				// now store the last find result for the next check
				lastLookupResult = descriptions;
			}
		}
	}
}
