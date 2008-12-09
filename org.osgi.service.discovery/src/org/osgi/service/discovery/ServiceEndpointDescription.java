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
 * This interface describes an endpoint of a service. This class can be
 * considered as a wrapper around the property map of a published service and
 * its endpoint. It provides an API to conveniently access the most important
 * properties of the service.
 * 
 * @version $Revision$
 */
public interface ServiceEndpointDescription {

	/**
	 * Returns the value of the property with key
	 * {@link ServicePublication#PROP_KEY_SERVICE_INTERFACE_NAME}.
	 * 
	 * @return service interface names provided by the advertised service
	 *         (endpoint). The collection is never null or empty but contains at
	 *         least one service interface.
	 */
	Collection /* <? extends String> */getProvidedInterfaces();

	/**
	 * Returns non-Java endpoint interface name associated with the given
	 * interface. Value of the property with key
	 * {@link ServicePublication#PROP_KEY_ENDPOINT_INTERFACE_NAME} is used by
	 * this operation.
	 * 
	 * @param interfaceName
	 *            for which its non-Java endpoint interface name should be
	 *            returned. 
	 *            
	 * @return non-Java endpoint interface name. Null, if it hasn't been
	 *         provided.
	 */
	String getEndpointInterfaceName(String interfaceName);

	/**
	 * Returns version of the given interface. Value of the property with key
	 * {@link ServicePublication#PROP_KEY_SERVICE_INTERFACE_VERSION} is used by
	 * this operation.
	 * 
	 * @param interfaceName
	 *            for which its version should be returned.
	 * @return Version of given service interface. Null, if it hasn't been
	 *         provided.
	 */
	String getVersion(String interfaceName);

	/**
	 * Returns the value of the property with key
	 * {@link ServicePublication#PROP_KEY_ENDPOINT_LOCATION}.
	 * 
	 * @return The URL of the service location. Null, if it hasn't been
	 *         provided.
	 */
	URL getLocation();

	/**
	 * Returns the value of the property with key
	 * {@link ServicePublication#PROP_KEY_ENDPOINT_ID}.
	 * 
	 * @return Unique id of service endpoint. Null, if it hasn't been provided.
	 */
	String getEndpointID();

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
