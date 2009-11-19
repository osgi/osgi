/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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
 * the a remote manager to control the export and import of services.
 * 
 * The API allows a remote manager to export a service, to import a service, and
 * find out about the current imports and exports.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface RemoteServiceAdmin {

	/**
	 * Export a service to a given endpoint. The Remote Service Admin must
	 * create an endpoint from the given description that can be used by other
	 * Distrbution Providers to connect to this Remote Service Admin and use the
	 * exported service. This method can return null if the service could not be
	 * exported because the endpoint could not be implemented by this Remote
	 * Service Admin.
	 * 
	 * TODO Peter to update for case insensitive properties
	 * 
	 * The properties on a Service Reference are case insensitive while the
	 * properties on a <code>properties</code> are case sensitive. A value in
	 * the <code>properties</code> must therefore override any case variant in
	 * the properties of the Service Reference.
	 * 
	 * <p>
	 * If the caller does not have the appropriate
	 * <code>EndpointPermission[endpoint,EXPORT]</code> for an endpoint, and the
	 * Java Runtime Environment supports permissions, then the
	 * {@link ExportRegistration#getException() getException} method on the
	 * corresponding returned {@link ExportRegistration} will return a
	 * <code>SecurityException</code>.
	 * 
	 * @param reference The Service Reference to export.
	 * @param properties The properties to create a local endpoint that can be
	 *        implemented by this Remote Service Admin. If this is null, the
	 *        endpoint will be determined by the properties on the service. The
	 *        properties are the same as given for an exported service. They are
	 *        overlaid over any properties the service defines (case
	 *        insensitive). This parameter can be <code>null</code>, this should
	 *        be treated as an empty map.
	 * 
	 *        TODO Peter The return description does not mesh with returning a
	 *        list! Why a list and not just one?
	 * @return An Export Registration that combines the Endpoint Description and
	 *         the Service Reference or <code>null</code> if the service could
	 *         not be exported.
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
	 * Import a service from an endpoint. The Remote Service Admin must use the
	 * given endpoint to create a proxy. This method can return null if the
	 * service could not be imported.
	 * 
	 * @param endpoint The Endpoint Description to be used for import.
	 * @return An Import Registration that combines the Endpoint Description and
	 *         the Service Reference or <code>null</code> if the endpoint could
	 *         not be imported.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>EndpointPermission[endpoint,IMPORT]</code> for the
	 *         endpoint, and the Java Runtime Environment supports permissions.
	 */
	ImportRegistration importService(EndpointDescription endpoint);

	/**
	 * Return the currently active Export References.
	 * 
	 * <p>
	 * If the caller does not have the appropriate
	 * <code>EndpointPermission[endpoint,READ]</code> for an endpoint, and the
	 * Java Runtime Environment supports permissions, then returned collection
	 * will not contain a reference to the exported endpoint.
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
	 * <code>EndpointPermission[endpoint,READ]</code> for an endpoint, and the
	 * Java Runtime Environment supports permissions, then returned collection
	 * will not contain a reference to the imported endpoint.
	 * 
	 * @return A <code>Collection</code> of {@link ImportReference}s that are
	 *         currently active.
	 */
	Collection<ImportReference> getImportedEndpoints();

}
