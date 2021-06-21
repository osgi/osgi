/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.rest.client;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.framework.startlevel.dto.BundleStartLevelDTO;
import org.osgi.framework.startlevel.dto.FrameworkStartLevelDTO;

/**
 * A Java client API for a REST service endpoint.
 * 
 * <p>
 * Provides a Java client API for accessing and managing a remote OSGi framework
 * through the REST API. Implementations of this interface will usually take the
 * URL to the remote REST Management Service instance as an argument in their
 * constructor. Further arguments might be needed, for example, if the cloud
 * provider requires URL signing.
 * 
 * @author $Id$
 */
@ProviderType
public interface RestClient {
	/**
	 * Retrieves the current framework start level.
	 * 
	 * @return Returns the current framework start level in the form of a
	 *         {@link FrameworkStartLevelDTO}.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	FrameworkStartLevelDTO getFrameworkStartLevel() throws Exception;

	/**
	 * Sets the current framework start level.
	 * 
	 * @param startLevel set the framework start level to this target.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void setFrameworkStartLevel(FrameworkStartLevelDTO startLevel) throws Exception;

	/**
	 * Get the bundles currently installed on the managed framework.
	 * 
	 * @return Returns a collection of the bundle URIs in the form of Strings.
	 *         The URIs are relative to the REST API root URL and can be used to
	 *         retrieve bundle representations.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	Collection<String> getBundlePaths() throws Exception;

	/**
	 * Get the bundle representations for all bundles currently installed in the
	 * managed framework.
	 * 
	 * @return Returns a collection of BundleDTO objects.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	Collection<BundleDTO> getBundles() throws Exception;

	/**
	 * Retrieve the bundle representation for a given bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return A {@link BundleDTO} for the requested bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleDTO getBundle(long id) throws Exception;

	/**
	 * Retrieve the bundle representation for a given bundle path.
	 * 
	 * @param bundlePath Addresses the bundle by its URI path.
	 * @return A {@link BundleDTO} for the requested bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleDTO getBundle(String bundlePath) throws Exception;

	/**
	 * Get the state for a given bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return Returns the current bundle state as defined in (@link
	 *         org.osgi.framework.Bundle}.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	int getBundleState(long id) throws Exception;

	/**
	 * Get the state for a given bundle path.
	 * 
	 * @param bundlePath Addresses the bundle by its URI path.
	 * @return Returns the current bundle state as defined in (@link
	 *         org.osgi.framework.Bundle}.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	int getBundleState(String bundlePath) throws Exception;

	/**
	 * Start a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void startBundle(long id) throws Exception;

	/**
	 * Start a bundle given by its URI path.
	 * 
	 * @param bundlePath Addresses the bundle by its URI path.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void startBundle(String bundlePath) throws Exception;

	/**
	 * Start a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @param options Passes additional options as defined in
	 *        {@link org.osgi.framework.Bundle#start(int)}
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void startBundle(long id, int options) throws Exception;

	/**
	 * Start a bundle given by its URI path.
	 * 
	 * @param bundlePath Addresses the bundle by its URI path.
	 * @param options Passes additional options as defined in
	 *        {@link org.osgi.framework.Bundle#start(int)}
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void startBundle(String bundlePath, int options) throws Exception;

	/**
	 * Stop a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void stopBundle(long id) throws Exception;

	/**
	 * Stop a bundle given by its URI path.
	 * 
	 * @param bundlePath Addresses the bundle by its URI path.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void stopBundle(String bundlePath) throws Exception;

	/**
	 * Stop a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @param options Passes additional options as defined in
	 *        {@link org.osgi.framework.Bundle#stop(int)}
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void stopBundle(long id, int options) throws Exception;

	/**
	 * Stop a bundle given by its URI path.
	 * 
	 * @param bundlePath Addresses the bundle by its URI path.
	 * @param options Passes additional options as defined in
	 *        {@link org.osgi.framework.Bundle#stop(int)}
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void stopBundle(String bundlePath, int options) throws Exception;

	/**
	 * Get the header for a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return Returns the map of headers entries.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	Map<String, String> getBundleHeaders(long id) throws Exception;

	/**
	 * Get the header for a bundle given by its URI path.
	 * 
	 * @param bundlePath Addresses the bundle by its URI path.
	 * @return Returns the map of headers entries.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	Map<String, String> getBundleHeaders(String bundlePath) throws Exception;

	/**
	 * Get the start level for a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return Returns a {@link BundleStartLevelDTO} describing the current
	 *         start level of the bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleStartLevelDTO getBundleStartLevel(long id) throws Exception;

	/**
	 * Get the start level for a bundle given by its URI path.
	 * 
	 * @param bundlePath Addresses the bundle by its URI path.
	 * @return Returns a {@link BundleStartLevelDTO} describing the current
	 *         start level of the bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleStartLevelDTO getBundleStartLevel(String bundlePath) throws Exception;

	/**
	 * Set the start level for a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @param startLevel The target start level.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void setBundleStartLevel(long id, int startLevel) throws Exception;

	/**
	 * 
	 * @param bundlePath Addresses the bundle by its URI path.
	 * @param startLevel The target start level.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	void setBundleStartLevel(String bundlePath, int startLevel) throws Exception;

	/**
	 * Install a new bundle given by an externally reachable location string,
	 * typically describing a URL.
	 * 
	 * @param location Passes the location string to retrieve the bundle content
	 *        from.
	 * @return Returns the {@link BundleDTO} of the newly installed bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleDTO installBundle(String location) throws Exception;

	/**
	 * Install a new bundle given by an {@link InputStream} to a bundle content.
	 * 
	 * @param location Passes the location string to be used to install the new
	 *        bundle.
	 * @param in Passes the input stream to a bundle.
	 * @return Returns the {@link BundleDTO} of the newly installed bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleDTO installBundle(String location, InputStream in) throws Exception;

	/**
	 * Uninstall a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return Returns the {@link BundleDTO} of the uninstalled bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleDTO uninstallBundle(long id) throws Exception;

	/**
	 * Uninstall a bundle given by its URI path.
	 * 
	 * @param bundlePath Addresses the bundle by its URI path.
	 * @return Returns the {@link BundleDTO} of the uninstalled bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleDTO uninstallBundle(String bundlePath) throws Exception;

	/**
	 * Updates a bundle given by its bundle Id using the bundle-internal update
	 * location.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return Returns the {@link BundleDTO} of the updated bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleDTO updateBundle(long id) throws Exception;

	/**
	 * Updates a bundle given by its URI path using the content at the specified
	 * URL.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @param url The URL whose content is to be used to update the bundle.
	 * @return Returns the {@link BundleDTO} of the updated bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleDTO updateBundle(long id, String url) throws Exception;

	/**
	 * Updates a bundle given by its bundle Id and passing the new bundle
	 * content in the form of an {@link InputStream}.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @param in Passes an input stream to the new bundle content.
	 * @return Returns the {@link BundleDTO} of the updated bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	BundleDTO updateBundle(long id, InputStream in) throws Exception;

	/**
	 * Gets a collection of URI paths to all installed services.
	 * 
	 * @return Returns a collection of URI paths to the installed services.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	Collection<String> getServicePaths() throws Exception;

	/**
	 * Gets a collection of URI paths to all installed services.
	 * 
	 * @param filter Passes a filter to restrict the result set.
	 * @return Returns a collection of URI paths to the installed services.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 * 
	 */
	Collection<String> getServicePaths(String filter) throws Exception;

	/**
	 * Get the service representations for all services.
	 * 
	 * @return Returns the service representations in the form of
	 *         {@link ServiceReferenceDTO} objects.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	Collection<ServiceReferenceDTO> getServiceReferences() throws Exception;

	/**
	 * Get the service representations for all services.
	 * 
	 * @param filter Passes a filter to restrict the result set.
	 * @return Returns the service representations in the form of
	 *         {@link ServiceReferenceDTO} objects.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	Collection<ServiceReferenceDTO> getServiceReferences(String filter) throws Exception;

	/**
	 * Get the service representation for a service given by its service Id.
	 * 
	 * @param id Addresses the service by its identifier.
	 * @return The service representation as {@link ServiceReferenceDTO}.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	ServiceReferenceDTO getServiceReference(long id) throws Exception;

	/**
	 * Get the service representation for a service given by its URI path.
	 * 
	 * @param servicePath Addresses the service by its URI path.
	 * @return The service representation as {@link ServiceReferenceDTO}.
	 * @throws Exception An exception representing a failure in the underlying
	 *         REST call.
	 */
	ServiceReferenceDTO getServiceReference(String servicePath) throws Exception;
}
