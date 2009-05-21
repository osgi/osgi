/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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

package org.osgi.service.remoteserviceadmin;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

/**
 * This interface describes an endpoint of a service. This class can be
 * considered as a wrapper around the property map of a published service and
 * its endpoint. It provides an API to conveniently access the most important
 * properties of the service.
 * <p>
 * <code>ServiceEndpointDescription</code> objects are immutable.
 * 
 * @Immutable
 * @version $Revision$
 */
public interface ServiceEndpointDescription {

	/**
	 * Returns the value of the property with key
	 * {@link ExportedService#SERVICE_INTERFACE_NAME}.
	 * 
	 * @return <code>Collection (&lt;String&gt;)</code> of service interface
	 *         names provided by the advertised service endpoint. The collection
	 *         is never <code>null</code> or empty but contains at least one
	 *         service interface.
	 */
	Collection /* <String> */getProvidedInterfaces();

	/**
	 * Returns non-Java endpoint interface name associated with the given
	 * interface.
	 * <p>
	 * Value of the property with key
	 * {@link ExportedService#ENDPOINT_INTERFACE_NAME} is used by this
	 * operation.
	 * 
	 * @param interfaceName for which its non-Java endpoint interface name
	 *        should be returned.
	 * @return non-Java endpoint interface name, or <code>null</code> if it
	 *         hasn't been provided or if given interface name is
	 *         <code>null</code>.
	 */
	String getEndpointInterfaceName(String interfaceName);

	/**
	 * Returns version of the given interface.
	 * <p>
	 * Value of the property with key
	 * {@link ExportedService#SERVICE_INTERFACE_VERSION} is used by this
	 * operation.
	 * 
	 * @param interfaceName for which its version should be returned.
	 * @return Version of given service interface, or <code>null</code> if it
	 *         hasn't been provided or if given interface name is
	 *         <code>null</code>.
	 */
	String getVersion(String interfaceName);

	/**
	 * Returns the value of the property with key
	 * {@link ExportedService#ENDPOINT_LOCATION}.
	 * 
	 * @return The url of the service location, or <code>null</code> if it
	 *         hasn't been provided.
	 */
	URI getLocation();

	/**
	 * Returns the value of the property with key
	 * {@link ExportedService#ENDPOINT_ID}.
	 * 
	 * @return Unique id of service endpoint, or <code>null</code> if it hasn't
	 *         been provided.
	 */
	String getEndpointID();

	/**
	 * Returns all service endpoint properties.
	 * 
	 * @return all properties of the service as a
	 *         <code>Map (&lt;String, Object&gt;)</code>. The map is never
	 *         <code>null</code> or empty but contains at least mandatory
	 *         <code>ServicePublication</code> properties. Since
	 *         <code>ServiceEndpointDescription</code> objects are immutable,
	 *         the returned map is also not going to be updated at a later point
	 *         of time.
	 */
	Map/* <String, Object> */getProperties();
}
