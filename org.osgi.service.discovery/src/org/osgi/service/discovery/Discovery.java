/*
 * $Date$
 *
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

package org.osgi.service.discovery;

/**
 * 
 * TODO: How to update published ServiceDescriptions? How to identify
 * ServiceDescriptions of the same service instance? <br>
 * TODO: how about to rename auto-publish to push/pull?<br>
 * TODO: how to propagate exceptions in async findService calls?
 * 
 * @version $Revision$
 */
public interface Discovery {
	final String ORG_OSGI_DISCOVERY = "org.osgi.discovery";

	final String ORG_OSGI_DISCOVERY_NONE = "none";

	final String ORG_OSGI_DISCOVERY_AUTO_PUBLISH = "auto-publish";

	/**
	 * Add a ServiceListener for a particular service description.
	 * 
	 * @param filter
	 *            a filter to services to listen for. If filter is
	 *            <code>null</code> then all services are considered.
	 * @param listener
	 *            which is to call when discovery detects changes in
	 *            availability or description of a service. The same listener
	 *            object may be used to listen on multiple service filters.
	 * @throws IllegalArgumentException
	 *             if listener is null or if filter is invalid
	 */
	void addServiceListener(ServiceListener listener, String filter);

	/**
	 * This method is the same as calling
	 * <code>Discovery.addServiceListener(ServiceListener listener, String filter)</code>
	 * with <code>filter</code> set to <code>null</code>.
	 * 
	 * @param listener
	 *            which is to call when discovery detects changes in
	 *            availability or description of a service. The same listener
	 *            object may be used to listen on multiple service filters.
	 * @throws IllegalArgumentException
	 *             if listener is null
	 * @see #addServiceListener(ServiceListener, String)
	 */
	void addServiceListener(ServiceListener listener);

	/**
	 * Removes a ServiceListener.
	 * 
	 * @param listener
	 *            ServiceListener which should be removed. If that listener
	 *            object was registered several times then all registrations
	 *            will be removed. If that listener object haven't been added
	 *            before, then the method returns without throwing exceptions.
	 */
	void removeServiceListener(ServiceListener listener);

	/**
	 * Searches for services matching the provided interface name and filter.
	 * 
	 * @param interfaceName
	 *            name of the interface returned services have to provide. If
	 *            name is null then all services are considered to match.
	 * @param filter
	 *            an LDAP filter which the service has to satisfy. Note that
	 *            <code>ServiceEndpointDescription</code> defines some
	 *            properties for service url, interface version etc.. If filter
	 *            is null all services are considered to match.
	 * @return Array of ServiceEndpointDescription objects matching the service
	 *         that was found to satisfy the find criteria. The Collection may
	 *         be empty if none was found.
	 */
	ServiceEndpointDescription[] findService(String interfaceName, String filter);

	/**
	 * Asynchronous version of
	 * <code>Discovery.findService(String interfaceName, String filter)</code>
	 * method.
	 * 
	 * @param interfaceName
	 *            name of the interface returned services have to provide. If
	 *            name is null then all services are considered to match.
	 * @param filter
	 *            an LDAP filter which the service has to satisfy. Note that
	 *            <code>ServiceEndpointDescription</code> defines some
	 *            properties for service url, interface version etc.. If filter
	 *            is null all services are considered to match.
	 * @param callback
	 *            to notify about the asynchronous response of the find
	 *            operation
	 * @throws IllegalArgumentException
	 *             if callback is null
	 * @see #findService(String, String)
	 */
	void findService(String interfaceName, String filter,
			FindServiceCallback callback);

	/**
	 * Publish the provided service description. If the property
	 * org.osgi.discovery = auto-publish, the Discovery implementation actively
	 * pushes the information about the service to the network. Otherwise, it is
	 * just available upon request from other Discovery implementations.
	 * 
	 * @param serviceEndpointDescription
	 *            ServiceEndpointDescription of the service to publish
	 * @return true if the service was successfully published.
	 * @throws IllegalArgumentException
	 *             if serviceEndpointDescription is null, incomplete or invalid
	 *             (e.g. contains unknown property types)
	 */
	boolean publishService(ServiceEndpointDescription serviceEndpointDescription);

	/**
	 * Publish the provided service. The information is published by the
	 * Discovery implementation.<b> If the parameter autopublish=true, the
	 * Discovery implementation actively pushes the information about the
	 * service to the network. Otherwise, it is just available upon request from
	 * other Discovery implementations. The ServiceEndpointDescription is
	 * matched using the Comparable interface.
	 * 
	 * @param serviceEndpointDescription
	 *            ServiceEndpointDescription of the service to publish
	 * @param autopublish
	 *            if true, service information is actively pushed to the network
	 *            for discovery
	 * @return true if the service was successfully published.
	 * @throws IllegalArgumentException
	 *             if serviceEndpointDescription is null, incomplete or invalid
	 *             (e.g. contains unknown property types)
	 */
	boolean publishService(
			ServiceEndpointDescription serviceEndpointDescription,
			boolean autopublish);

	/**
	 * Make the given service un-discoverable. The previous publish request for
	 * a service is undone. The service information is also removed from the
	 * local or global cache if cached before.
	 * 
	 * @param serviceEndpointDescription
	 *            ServiceEndpointDescription of the service to unpublish. If
	 *            this ServiceEndpointDescription haven't been published before,
	 *            then the method returns without throwing exceptions.
	 * @throws IllegalArgumentException
	 *             if serviceEndpointDescription is null or incomplete
	 */
	void unpublishService(ServiceEndpointDescription serviceEndpointDescription);
}
