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

package org.osgi.service.remoteservices;

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
public interface EndpointDescription {

	/**
	 * Contains a collection of fully qualified interface names offered by this
	 * endpoint.
	 * 
	 * @return <code>Collection (&lt;String&gt;)</code> of service interface
	 *         names provided by the advertised service endpoint. The collection
	 *         is never <code>null</code> or empty but contains at least one
	 *         service interface.
	 */
	Collection /* <InterfaceDescription> */getInterfaceDescriptions();

	/**
	 * Returns the URI of the service location of this endpoint.
	 * 
	 * @return The URI of the service location, or <code>null</code> if it
	 *         hasn't been provided.
	 */
	URI getURI();

	/**
	 * Returns the universally unique id for the service represented by this
	 * endpoint.
	 * 
	 * @return Unique id of service endpoint, or <code>null</code> if it hasn't
	 *         been provided.
	 */
	String getServiceUUID();
	
	/**
	 * Returns the configuration
	 * 
	 * @return
	 * @see RemoteServiceConstants#REMOTE_CONFIGS_SUPPORTED
	 */
	String getConfigurationType();

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
