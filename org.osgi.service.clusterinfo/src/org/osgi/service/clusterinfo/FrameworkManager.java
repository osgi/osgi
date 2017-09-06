/*
 * Copyright (c) OSGi Alliance (2010, 2017). All Rights Reserved.
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
package org.osgi.service.clusterinfo;

import java.util.Collection;
import java.util.Map;

import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.framework.startlevel.dto.BundleStartLevelDTO;
import org.osgi.framework.startlevel.dto.FrameworkStartLevelDTO;

/**
 * Provides a management interface for accessing and managing a remote OSGi
 * framework. This interface can be accessed remotely via Remote Services.
 */
public interface FrameworkManager {
	
	/**
	 * Retrieve the bundle representation for a given bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return A {@link BundleDTO} for the requested bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	BundleDTO getBundle(long id) throws Exception;
	
	/**
	 * Get the header for a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return Returns the map of headers entries.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	Map<String,String> getBundleHeaders(long id) throws Exception;
	
	/**
	 * Get the bundle representations for all bundles currently installed in the
	 * managed framework.
	 * 
	 * @return Returns a collection of BundleDTO objects.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	Collection<BundleDTO> getBundles() throws Exception;
	
	/**
	 * Get the start level for a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return Returns a {@link BundleStartLevelDTO} describing the current
	 *         start level of the bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	BundleStartLevelDTO getBundleStartLevel(long id) throws Exception;
	
	/**
	 * Get the state for a given bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return Returns the current bundle state as defined in (@link
	 *         org.osgi.framework.Bundle}.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	int getBundleState(long id) throws Exception;
	
	/**
	 * Retrieves the current framework start level.
	 * 
	 * @return Returns the current framework start level in the form of a
	 *         {@link FrameworkStartLevelDTO}.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	FrameworkStartLevelDTO getFrameworkStartLevel() throws Exception;
	
	/**
	 * Get the service representation for a service given by its service Id.
	 * 
	 * @param id Addresses the service by its identifier.
	 * @return The service representation as {@link ServiceReferenceDTO}.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	ServiceReferenceDTO getServiceReference(long id) throws Exception;
	
	/**
	 * Get the service representations for all services.
	 * 
	 * @return Returns the service representations in the form of
	 *         {@link ServiceReferenceDTO} objects.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	Collection<ServiceReferenceDTO> getServiceReferences() throws Exception;
	
	/**
	 * Get the service representations for all services.
	 * 
	 * @param filter Passes a filter to restrict the result set.
	 * @return Returns the service representations in the form of
	 *         {@link ServiceReferenceDTO} objects.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	Collection<ServiceReferenceDTO> getServiceReferences(String filter) throws Exception;
	
	/**
	 * Install a new bundle given by an externally reachable location string,
	 * typically describing a URL.
	 * 
	 * @param location Passes the location string to retrieve the bundle content
	 *            from.
	 * @return Returns the {@link BundleDTO} of the newly installed bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	BundleDTO installBundle(String location) throws Exception;
	
	/**
	 * Set the start level for a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @param startLevel The target start level.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	void setBundleStartLevel(long id, int startLevel) throws Exception;
	
	/**
	 * Sets the current framework start level.
	 * 
	 * @param startLevel set the framework start level to this target.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	void setFrameworkStartLevel(FrameworkStartLevelDTO startLevel)
			throws Exception;
	
	/**
	 * Start a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	void startBundle(long id) throws Exception;
	
	/**
	 * Start a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @param options Passes additional options as defined in
	 *            {@link org.osgi.framework.Bundle#start(int)}
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	void startBundle(long id, int options) throws Exception;
	
	/**
	 * Stop a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	void stopBundle(long id) throws Exception;
	
	/**
	 * Stop a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @param options Passes additional options as defined in
	 *            {@link org.osgi.framework.Bundle#stop(int)}
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	void stopBundle(long id, int options) throws Exception;
	
	/**
	 * Uninstall a bundle given by its bundle Id.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return Returns the {@link BundleDTO} of the uninstalled bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
	 */
	BundleDTO uninstallBundle(long id) throws Exception;
	
	/**
	 * Updates a bundle given by its bundle Id using the bundle-internal update
	 * location.
	 * 
	 * @param id Addresses the bundle by its identifier.
	 * @return Returns the {@link BundleDTO} of the updated bundle.
	 * @throws Exception An exception representing a failure in the underlying
	 *             remote call.
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
	 *             remote call.
	 */
	BundleDTO updateBundle(long id, String url) throws Exception;
	
}
