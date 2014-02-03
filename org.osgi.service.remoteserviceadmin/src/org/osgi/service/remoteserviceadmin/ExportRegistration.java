/*
 * Copyright (c) OSGi Alliance (2009, 2013). All Rights Reserved.
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

import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.ServiceReference;

/**
 * An Export Registration associates a service to a local endpoint.
 * 
 * The Export Registration can be used to delete the endpoint associated with an
 * this registration. It is created with the
 * {@link RemoteServiceAdmin#exportService(ServiceReference,Map)} method.
 * 
 * When this Export Registration has been closed, all methods must return
 * {@code null}.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface ExportRegistration {
	/**
	 * Return the Export Reference for the exported service.
	 * 
	 * @return The Export Reference for this registration, or <code>null</code>
	 *         if this Import Registration is closed.
	 * @throws IllegalStateException When this registration was not properly
	 *         initialized. See {@link #getException()}.
	 */
	ExportReference getExportReference();

	/**
	 * Update the endpoint represented by this {@link ExportRegistration} and
	 * return an updated {@link EndpointDescription}. If this method returns an
	 * updated {@link EndpointDescription}, then the object returned via
	 * {@link #getExportReference()} must also have been updated to return this
	 * new object. If this method does not return an updated
	 * {@link EndpointDescription} then the object returned via
	 * {@link #getExportReference()} should remain unchanged.
	 * 
	 * When creating the updated {@link EndpointDescription} the
	 * {@link ServiceReference} originally passed to
	 * {@link RemoteServiceAdmin#exportService(ServiceReference, Map)} must be
	 * queried to pick up any changes to its service properties.
	 * 
	 * If this argument is null then the original properties passed when
	 * creating this ExportRegistration should be used when constructing the
	 * updated {@link EndpointDescription}. Otherwise the new properties should
	 * be used, and replace the original properties for subsequent calls to the
	 * update method.
	 * 
	 * @param properties properties to be merged with the current service
	 *        properties for the {@link ServiceReference} represented by this
	 *        {@link ExportRegistration}. If null is passed then the original
	 *        properties passed to
	 *        {@link RemoteServiceAdmin#exportService(ServiceReference, Map)}
	 *        will be used.
	 * @return The updated {@link EndpointDescription} for this registration or
	 *         null if there was a failure updating the endpoint. If a failure
	 *         occurs then it can be accessed using {@link #getException()}.
	 * @throws IllegalStateException If this registration is closed, or when
	 *         this registration was not properly initialized. See
	 *         {@link #getException()}.
	 * 
	 * @since 1.1
	 */
	EndpointDescription update(Map<String, ?> properties);

	/**
	 * Delete the local endpoint and disconnect any remote distribution
	 * providers. After this method returns, all methods must return
	 * {@code null}.
	 * 
	 * This method has no effect when this registration has already been closed
	 * or is being closed.
	 */
	void close();

	/**
	 * Return the exception for any error during the export process.
	 * 
	 * If the Remote Service Admin for some reasons is unable to properly
	 * initialize this registration, then it must return an exception from this
	 * method. If no error occurred, this method must return {@code null}.
	 * 
	 * The error must be set before this Export Registration is returned.
	 * Asynchronously occurring errors must be reported to the log.
	 * 
	 * @return The exception that occurred during the initialization of this
	 *         registration or {@code null} if no exception occurred.
	 */
	Throwable getException();
}
