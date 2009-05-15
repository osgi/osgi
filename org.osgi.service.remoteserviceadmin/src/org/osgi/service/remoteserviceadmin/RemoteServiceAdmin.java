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

import java.util.Collection;

import org.osgi.framework.ServiceReference;

/**
 * The Remote Service Admin service. This service is registered by a
 * distribution provider with the {@link #REMOTE_INTENTS_SUPPORTED} and
 * {@link #REMOTE_CONFIGURATION_TYPES_SUPPORTED} service properties to denote
 * the intents and configuration types supported by this distribution provider.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface RemoteServiceAdmin {
	/**
	 * Service property that lists the intents supported by this Remote Service
	 * Admin.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	static final String	REMOTE_INTENTS_SUPPORTED				= "remote.intents.supported";

	/**
	 * Service property that lists the configuration types supported by this
	 * Remote Service Admin.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	static final String	REMOTE_CONFIGURATION_TYPES_SUPPORTED	= "remote.configuration.types.supported";

	/**
	 * Returns references to the imported services. The returned references are
	 * to services which are local proxies to remote services that have been
	 * imported.
	 * 
	 * @return A collection of Service References to the imported services. An
	 *         empty collection is returned if this Remote Service Admin has not
	 *         imported any services.
	 */
	Collection /* <ServiceReference> */getImportedServices();

	/**
	 * Returns references to the services which have been exported. The returned
	 * references are to local services which have been made remotely available.
	 * 
	 * @return A collection of Service References to the exported services. An
	 *         empty collection is returned if this Remote Service Admin has not
	 *         exported any services.
	 */
	Collection /* <ServiceReference> */getExportedServices();

	/**
	 * Return the Service Endpoint Description for the specified exported
	 * service.
	 * 
	 * @param exported A Service Reference to a service exported by this Remote
	 *        Service Admin.
	 * @return The Service Endpoint Description for the specified exported
	 *         service or <code>null</code> if the specified service is not
	 *         exported by this Remote Service Admin.
	 */
	ServiceEndpointDescription getServiceEndpointDescription(
			ServiceReference exported);
}
