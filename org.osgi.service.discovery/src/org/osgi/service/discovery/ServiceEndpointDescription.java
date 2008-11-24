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

import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * The ServiceEndpointDescription interface describes an endpoint of a service.
 * This class can be considered as a wrapper around the property map associated
 * with a service and its endpoint. It provides an API to conveniently access
 * the most important properties of the service.
 * 
 * @version $Revision$
 */
public interface ServiceEndpointDescription {
	/**
	 * Property identifying (non-Java) endpoint interface. Property value is of type String.
	 */
	public final String PROP_KEY_ENDPOINT_INTERFACE_NAME = "osgi.remote.endpoint.interface";

	/**
	 * Property uniquely identifying the endpoint. 
	 */
	public final String PROP_KEY_ENDPOINT_ID = "osgi.remote.endpoint.id";

	/**
	 * Property identifying service version.
	 */
	public final String PROP_KEY_VERSION = "service.version";

	/**
	  * Property identifying service location.
	 */
	public final String PROP_KEY_LOCATION = "osgi.remote.endpoint.location";

	/**
	 * @return full qualified service interface names provided by the advertised
	 *         service (endpoint). The collection is never null or empty but
	 *         contains at least one service interface.
	 */
	Collection /* <? extends String> */getInterfaceNames();

	/**
	 * @param interfaceName
	 *            for which its communication protocol specific version should
	 *            be returned. It might be for instance a web service interface
	 *            name. Though this information is usually contained in
	 *            according interface descriptions, e.g. a wsdl file, it can
	 *            optionally be provided here as well since discovery usually
	 *            doesn't read and interprets such accompanying descriptions.
	 * 
	 * @return The protocol specific service interface name.
	 */
	String getEndpointInterfaceName(String interfaceName);

	/**
	 * @param interfaceName
	 *            for which its version should be returned.
	 * @return The service interface/implementation version.
	 */
	String getVersion(String interfaceName);

	/**
	 * @return The URL of the service location.
	 */
	URL getLocation();

	/**
	 * Getter method for the property value of a given key.
	 * 
	 * @param key
	 *            Name of the property
	 * @return The property value, null if none is found for the given key
	 */
	Object getProperty(String key);

	/**
	 * @return <code>java.util.Collection</code> of property names available in
	 *         the ServiceEndpointDescription. The collection is never null or
	 *         empty but contains at least basic properties like objectClass for
	 *         the service interface. The collection represents a snapshot and
	 *         as such is not going to be updated in case properties were added
	 *         or removed at a later point of time.
	 */
	Collection/* <? extends String> */getPropertyKeys();

	/**
	 * @return Returns all properties of the service as a
	 *         <code>java.util.Map</code>. The map is never null or empty but
	 *         contains at least basic properties like objectClass for the
	 *         service interface. The collection represents a snapshot and as
	 *         such is not going to be updated in case properties were added or
	 *         removed at a later point of time.
	 */
	Map/* <String, Object> */getProperties();
}
