/*
 * Copyright (c) OSGi Alliance (2009, 2010). All Rights Reserved.
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
import java.util.Map;

import org.osgi.framework.ServiceReference;

/**
 * A Remote Service Admin manages the import and export of services.
 * 
 * A Distribution Provider can expose a control interface. This interface allows
 * a Topology Manager to control the export and import of services.
 * 
 * The API allows a Topology Manager to export a service, to import a service,
 * and find out about the current imports and exports.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface RemoteServiceAdmin {

	/**
	 * Export a service to a given Endpoint. The Remote Service Admin must
	 * create an Endpoint from the given description that can be used by other
	 * Distribution Providers to connect to this Remote Service Admin and use
	 * the exported service.
	 * 
	 * The property keys of a Service Reference are case insensitive while the
	 * property keys of the specified <code>properties</code> map are case
	 * sensitive. A property key in the specified <code>properties</code> map
	 * must therefore override any case variant property key in the properties
	 * of the specified Service Reference.
	 * 
	 * <p>
	 * If the caller does not have the appropriate
	 * <code>EndpointPermission[endpoint,EXPORT]</code> for an Endpoint, and the
	 * Java Runtime Environment supports permissions, then the
	 * {@link ExportRegistration#getException() getException} method on the
	 * corresponding returned {@link ExportRegistration} will return a
	 * <code>SecurityException</code>.
	 * 
	 * @param reference The Service Reference to export.
	 * @param properties The properties to create a local Endpoint that can be
	 *        implemented by this Remote Service Admin. If this is
	 *        <code>null</code>, the Endpoint will be determined by the
	 *        properties on the service. The properties are the same as given
	 *        for an exported service. They override any properties in the
	 *        specified Service Reference (case insensitive). The properties
	 *        <code>objectClass</code> and <code>service.id</code>, in any case
	 *        variant, are ignored. Those properties in the Service Reference
	 *        cannot be overridden. This parameter can be <code>null</code>,
	 *        this should be treated as an empty map.
	 * @return A <code>Collection</code> of {@link ExportRegistration}s for the
	 *         specified Service Reference and properties. Multiple Export
	 *         Registrations may be returned because a single service can be
	 *         exported to multiple Endpoints depending on the available
	 *         configuration type properties. The result is never
	 *         <code>null</code> but may be empty if this Remove Service Admin
	 *         does not recognize any of the configuration types.
	 * @throws IllegalArgumentException If any of the properties has a value
	 *         that is not syntactically correct or if the service properties
	 *         and the overlaid properties do not contain a
	 *         {@link RemoteConstants#SERVICE_EXPORTED_INTERFACES} entry.
	 * @throws UnsupportedOperationException If any of the intents expressed
	 *         through the properties is not supported by the distribution
	 *         provider.
	 */
	Collection<ExportRegistration> exportService(ServiceReference reference,
			Map<String, Object> properties);

	/**
	 * Import a service from an Endpoint. The Remote Service Admin must use the
	 * given Endpoint to create a proxy. This method can return
	 * <code>null</code> if the service could not be imported.
	 * 
	 * @param endpoint The Endpoint Description to be used for import.
	 * @return An Import Registration that combines the Endpoint Description and
	 *         the Service Reference or <code>null</code> if the Endpoint could
	 *         not be imported.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>EndpointPermission[endpoint,IMPORT]</code> for the
	 *         Endpoint, and the Java Runtime Environment supports permissions.
	 */
	ImportRegistration importService(EndpointDescription endpoint);

	/**
	 * Return the currently active Export References.
	 * 
	 * <p>
	 * If the caller does not have the appropriate
	 * <code>EndpointPermission[endpoint,READ]</code> for an Endpoint, and the
	 * Java Runtime Environment supports permissions, then returned collection
	 * will not contain a reference to the exported Endpoint.
	 * 
	 * @return A <code>Collection</code> of {@link ExportReference}s that are
	 *         currently active.
	 */
	Collection<ExportReference> getExportedServices();

	/**
	 * Return the currently active Import References.
	 * 
	 * <p>
	 * If the caller does not have the appropriate
	 * <code>EndpointPermission[endpoint,READ]</code> for an Endpoint, and the
	 * Java Runtime Environment supports permissions, then returned collection
	 * will not contain a reference to the imported Endpoint.
	 * 
	 * @return A <code>Collection</code> of {@link ImportReference}s that are
	 *         currently active.
	 */
	Collection<ImportReference> getImportedEndpoints();

}
