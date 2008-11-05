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

package org.osgi.service.discovery;

import java.util.Collection;
import java.util.Map;

/**
 * Interface of the Discovery service. This service allows to publish services
 * exposed for remote access as well as search for remote services. <BR>
 * Discovery service implementations usually rely on some discovery protocols or
 * other information distribution means.
 * 
 * @version $Revision$
 */
public interface Discovery {
	/**
	 * 
	 */
	final String OSGI_DISCOVERY = "osgi.discovery";

	/**
	 * 
	 */
	final String OSGI_DISCOVERY_NONE = "none";

	/**
	 * 
	 */
	final String OSGI_DISCOVERY_AUTO_PUBLISH = "auto-publish";

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
	 *            name of the interface that returned services have to provide.
	 *            If name is null then all services are considered to match.
	 * @param filter
	 *            an LDAP filter which the service has to satisfy. If filter is
	 *            null all services are considered to match.
	 * @return Collection of <code>ServiceEndpointDescription</code> objects
	 *         which were found to match interface name and filter. The
	 *         collection is empty if none was found. The collection represents
	 *         a snapshot and as such is not going to be updated in case other matching
	 *         services become available at a later point of time.
	 */
	Collection /* <? extends ServiceEndpointDescription> */findService(
			String interfaceName, String filter);

	/**
	 * Asynchronous version of <code>Discovery.findService(String interfaceName,
	 * String filter)</code> method.
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
	 * Publish the provided service meta-data.
	 * 
	 * @param javaInterfacesAndVersions
	 *            names of java interfaces offered by the service and their
	 *            version. For every interface to publish you have to define its
	 *            version. If you don't have a version, put "0.0.0" in it.
	 * @param javaInterfacesAndEndpointInterfaces
	 *            associates java interfaces to general end point interface
	 *            names. It is not needed to to have and end point interface for
	 *            a java interface. The map may be null.
	 * @param properties
	 *            a bag of service properties (key-value pairs) to be published.
	 *            It may be null. Note that Discovery might make use of certain
	 *            standard properties like the ones defined by
	 *            {@link ServiceEndpointDescription} for the publication process
	 *            if they are provided.
	 * 
	 * @return an instance of {@link ServiceEndpointDescription} or null if the
	 *         publishing failed
	 * 
	 * @throws IllegalArgumentException
	 *             if javaInterfacesAndVersions is null or empty
	 */
	ServiceEndpointDescription publishService(
			Map/* <String, String> */javaInterfacesAndVersions,
			Map/* <String, String> */javaInterfacesAndEndpointInterfaces,
			Map/* <String, Object> */properties);

	/**
	 * Publish the provided service. The information is published by the
	 * Discovery implementation.<b> If the parameter autopublish=true, the
	 * Discovery implementation actively pushes the information about the
	 * service to the network. Otherwise, it is just available upon request from
	 * other Discovery implementations. The ServiceEndpointDescription is
	 * matched using the Comparable interface.
	 * 
	 * @param javaInterfacesAndVersions
	 *            its an association between interfaces and versions. For every
	 *            interface to publish you have to define its version. If you
	 *            don't have a version, put "0.0.0" in it.
	 * @param javaInterfacesAndEndpointInterfaces
	 *            associates java interfaces to general end point interface
	 *            names. It is not needed to to have and end point interface for
	 *            a java interface. The map can be null.
	 * @param properties
	 *            a bag of properties to be published; can be null
	 * @param autopublish
	 *            if true, service information is actively pushed to the network
	 *            for discovery
	 * 
	 * @return an instance of {@link ServiceEndpointDescription} or null if the
	 *         publishing failed
	 * 
	 * @throws IllegalArgumentException
	 *             if javaInterfacesAndVersions is null or empty
	 */
	ServiceEndpointDescription publishService(
			Map/* <String, String> */javaInterfacesAndVersions,
			Map/* <String, String> */javaInterfacesAndEndpointInterfaces,
			Map/* <String, Object> */properties, boolean autopublish);

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
