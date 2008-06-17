/*
 * $Date: 2008-04-02 12:42:59 -0800 $
 *
 * Copyright (c) OSGi Alliance (2004, 2007). All Rights Reserved.
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

package org.osgi.service.discovery;

import java.util.Collection;

/**
 * 
 * 
 */
public interface Discovery {
	final String ORG_OSGI_DISCOVERY = "org.osgi.discovery";
	final String ORG_OSGI_DISCOVERY_NONE = "none";
	final String ORG_OSGI_DISCOVERY_AUTO_PUBLISH = "auto-publish";
	
	/**
	 * Add a ServiceListener
	 * 
	 * @param listener
	 */
	void addServiceListener(ServiceListener listener);
	
	/**
	 * Remove a ServiceListener
	 * 
	 * @param listener
	 */
	void removeServiceListener(ServiceListener listener);
	
	/**
	 * Compares the given ServiceDescription with the information in the local or global cache (depending on the
	 * cache strategy set in the properties of the Discovery implementation).<b>
	 * The ServiceDescription is matched using the Comparable interface.
	 * @param serviceDescription
	 * @return true if a service matching the given serviceDescription is found in the local cache
	 * @throws IllegalArgumentException if serviceDescription is null or incomplete
	 */
	boolean isCached(ServiceDescription serviceDescription);
	
	/**
	 * Returns an array of all ServiceDescription objects currently known to the Discovery implementation.
	 * @return An array of ServiceDescription objects. An empty array is returned if no service description
	 * was found.
	 */
	ServiceDescription[] getCachedServiceDescriptions();
	
	/**
	 * Asynchronous interface to initiate the search for an suitable service based on the provided ServiceDescription.
	 * The ServiceDescription is matched using the Comparable interface.
	 * @param serviceDescription ServiceDescription of the service to locate
	 * @param callback Listener object to notify about the asynchronous response of the find operation
	 * @throws IllegalArgumentException if the serviceDescription is null or incomplete
	 */
	void findService(ServiceDescription serviceDescription, ServiceListener callback);
	
	/**
	 * Find a service based on the provided service description. The match is performed through the Comparable interface
	 * implementation of ServiceDescription.
	 * @param serviceDescription ServiceDescription of the service to locate
	 * @return Collection of ServiceDescription objects matching the service that was found to satisfy the find criteria.
	 *         The Collection may be empty if none was found
	 * @throws IllegalArgumentException if serviceDescription is null or incomplete
	 */
	Collection findService(ServiceDescription serviceDescription);
	
	/**
	 * Publish the provided service. The information is published by the Discovery implementation.<b>
	 * If the property osgi.discovery = auto-publish, the Discovery implementation actively pushes the
	 * information about the service to the network. Otherwise, it is just available upon request from other
	 * Discovery implementations.
	 * The ServiceDescription is matched using the Comparable interface.
	 * @param serviceDescription ServiceDescription of the service to publish
	 * @return true if the service was successfully published.
	 * @throws IllegalArgumentException if serviceDescription is null or incomplete
	 */
	boolean publish(ServiceDescription serviceDescription);

	/**
	 * Publish the provided service. The information is published by the Discovery implementation.<b>
	 * If the parameter autopublish=true, the Discovery implementation actively pushes the
	 * information about the service to the network. Otherwise, it is just available upon request from other
	 * Discovery implementations.
	 * The ServiceDescription is matched using the Comparable interface.
	 * @param serviceDescription ServiceDescription of the service to publish
	 * @param autopublish if true, service information is actively pushed to the network for discovery
	 * @return true if the service was successfully published.
	 * @throws IllegalArgumentException if serviceDescription is null or incomplete
	 */
	boolean publish(ServiceDescription serviceDescription, boolean autopublish);
	
	/**
	 * Make the given service un-discoverable. The previous publish request for a service is undone. The service
	 * information is removed from the local or global cache.
	 * The ServiceDescription is matched using the Comparable interface.
	 * @param serviceDescription ServiceDescription of the service to unpublish
	 * @throws IllegalArgumentException if serviceDescription is null or incomplete
	 */
	void unpublish(ServiceDescription serviceDescription);
}
