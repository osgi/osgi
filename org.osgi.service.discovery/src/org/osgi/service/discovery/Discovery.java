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
 * exposed for remote access as well as to search for remote services. <BR>
 * Discovery service implementations usually rely on some discovery protocols or
 * other information distribution means.
 * 
 * @version $Revision$
 */
public interface Discovery {

	/**
	 * Property identifying Discovery's default strategy for distribution of published
	 * service information. It's up to the Discovery service to provide and
	 * support this property. Value of this property is of type String.
	 */
	public static final String PROP_KEY_PUBLISH_STRATEGY = "osgi.discovery.strategy.publication";

	/**
	 * Constant for a "push" publication strategy: published service information is
	 * actively pushed to the network for discovery.
	 */
	public static final String PROP_VAL_PUBLISH_STRATEGY_PUSH = "push";

	/**
	 * Constant for a "pull" publication strategy: published service information is
	 * available just upon lookup requests.
	 */
	public static final String PROP_VAL_PUBLISH_STRATEGY_PULL = "pull";

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
	 *         a snapshot and as such is not going to be updated in case other
	 *         matching services become available at a later point of time.
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
	 * Publish the provided service meta-data. <br>
	 * 
	 * Corresponds to calling {@link #publishService(Map, Map, Map, String)}
	 * with default <code>publishStrategy</code>
	 * 
	 * @param javaInterfacesAndVersions
	 *            names of java interfaces offered by the service and their
	 *            version. Version has to be provided for every interface to
	 *            publish. If version is unknown, use String-value of
	 *            <code>org.osgi.framework.Version.emptyVersion</code> constant.
	 * @param javaInterfacesAndEndpointInterfaces
	 *            associates java interfaces with endpoint's non-java interfaces
	 *            names. It is not mandatory to provide an endpoint interface
	 *            for a java interface. The map may be null.
	 * @param properties
	 *            a bag of service properties (key-value pairs) to be published.
	 *            It may be null. Note that Discovery might make use of certain
	 *            standard properties like the ones defined by
	 *            {@link ServiceEndpointDescription} for the publication process
	 *            if they are provided.
	 * 
	 * @return an instance of <code>ServiceEndpointDescription</code> or null if
	 *         the publishing failed
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>javaInterfacesAndVersions</code> is null or empty
	 */
	ServiceEndpointDescription publishService(
			Map/* <String, String> */javaInterfacesAndVersions,
			Map/* <String, String> */javaInterfacesAndEndpointInterfaces,
			Map/* <String, Object> */properties);

	/**
	 * Publish the provided service meta-data. <br>
	 * 
	 * @param javaInterfacesAndVersions
	 *            names of java interfaces offered by the service and their
	 *            version. Version has to be provided for every interface to
	 *            publish. If version is unknown, use String-value of
	 *            <code>org.osgi.framework.Version.emptyVersion</code> constant.
	 * @param javaInterfacesAndEndpointInterfaces
	 *            associates java interfaces with endpoint's non-java interfaces
	 *            names. It is not mandatory to provide an endpoint interface
	 *            for a java interface. The map may be null.
	 * @param properties
	 *            a bag of service properties (key-value pairs) to be published.
	 *            It may be null. Note that Discovery might make use of certain
	 *            standard properties, e.g. the ones defined by
	 *            {@link ServiceEndpointDescription}, for the publication
	 *            process if they are provided.
	 * @param publishStrategy
	 *            strategy for distribution of the published service
	 *            information. It may be {@link #PROP_VAL_PUBLISH_STRATEGY_PULL}
	 *            , {@link #PROP_VAL_PUBLISH_STRATEGY_PUSH} or any other
	 *            strategy supported by Discovery.
	 * 
	 * @return an instance of <code>ServiceEndpointDescription</code> or null if
	 *         the publishing failed
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>javaInterfacesAndVersions</code> is null or empty or
	 *             if not supported <code>publishStrategy</code> has been
	 *             provided.
	 */
	ServiceEndpointDescription publishService(
			Map/* <String, String> */javaInterfacesAndVersions,
			Map/* <String, String> */javaInterfacesAndEndpointInterfaces,
			Map/* <String, Object> */properties, String publishStrategy);

	/**
	 * Updates a service publication. <br>
	 * Depending on Discovery's implementation and underlying protocol it may
	 * result in an update or new re-publication of the service.
	 * 
	 * @param serviceEndpointDescription
	 *            identifies the previously published service whose publication
	 *            needs to be updated.
	 * @param newProperties
	 *            new set of service endpoint properties. It may be null.
	 * 
	 * @return new ServiceEndpointDescription identifying the published service
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>serviceEndpointDescription</code> is null or
	 *             invalid.
	 */
	ServiceEndpointDescription updateService(
			ServiceEndpointDescription serviceEndpointDescription,
			Map/* <String, Object> */newProperties);

	/**
	 * Make the given service un-discoverable. The previous publish request for
	 * a service is undone.
	 * 
	 * @param serviceEndpointDescription
	 *            ServiceEndpointDescription of the service to unpublish.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>serviceEndpointDescription</code> is null or
	 *             invalid.
	 */
	void unpublishService(ServiceEndpointDescription serviceEndpointDescription);
}
