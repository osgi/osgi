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
 * <code>EndpointDescription</code> objects are immutable.
 * 
 * @Immutable
 * @version $Revision$
 */
public interface EndpointDescription {
	/**
	 * Returns a collection if {@link InterfaceDescription} objects offered by
	 * this endpoint.
	 * 
	 * @return <code>Collection (&lt;InterfaceDescription&gt;)</code> objects
	 *         offered by this endpoint. The collection is never
	 *         <code>null</code> or empty. It contains at least one service
	 *         entry.
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
	 * endpoint. If the same service is available over multiple endpoints, all
	 * endpoints share the same UUID.
	 * 
	 * @return Unique id of service, or <code>null</code> if it hasn't been
	 *         provided.
	 */
	String getServiceUUID();

	/**
	 * Returns the configuration type used to describe the endpoint.
	 * 
	 * @return The configuration type.
	 * @see RemoteServiceConstants#SERVICE_EXPORTED_CONFIGS
	 * @see RemoteServiceConstants#SERVICE_IMPORTED_CONFIGS
	 * @see DistributionProviderConstants#REMOTE_CONFIGS_SUPPORTED
	 */
	String getConfigurationType();

	/**
	 * Returns all service endpoint properties. This map will include the
	 * original service properties, potentially augmented by properties added by
	 * the Distribution Provider to complete the metadata required on the
	 * consuming side for the Configuration Type.
	 * 
	 * @return An immutable Map(&lt;String, Object&gt;) containing all the
	 *         properties. Never returns <code>null</code>.
	 */
	Map/* <String, Object> */getProperties();
}
