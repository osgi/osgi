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

import org.osgi.framework.InvalidSyntaxException;

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
	 * ServiceRegistration property identifying Discovery's default strategy for distribution of
	 * published service information. It's up to the Discovery service to
	 * provide and support this property. Value of this property is of type
	 * String.
	 */
	public static final String PROP_KEY_PUBLISH_STRATEGY = "osgi.discovery.strategy.publication";

	/**
	 * Constant for a "push" publication strategy: published service information
	 * is actively pushed to the network for discovery.
	 */
	public static final String PROP_VAL_PUBLISH_STRATEGY_PUSH = "push";

	/**
	 * Constant for a "pull" publication strategy: published service information
	 * is available just upon lookup requests.
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
	 * @throws InvalidSyntaxException
	 *             if filter contains an invalid filter string that cannot be
	 *             parsed.
	 */
	Collection /* <? extends ServiceEndpointDescription> */findService(
			String interfaceName, String filter) throws InvalidSyntaxException;

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
	 * @throws InvalidSyntaxException
	 *             if filter contains an invalid filter string that cannot be
	 *             parsed.
	 * @throws IllegalArgumentException
	 *             if callback is null
	 * 
	 * @see #findService(String, String)
	 */
	void findService(String interfaceName, String filter,
			FindServiceCallback callback) throws InvalidSyntaxException;
}
