/*
 * $Date$
 *
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

/**
 * 
 * TODO: How to update published ServiceDescriptions? How to identify ServiceDescriptions of the same service instance?
 * TODO: how about to rename auto-publish to push/pull?
 * 
 * @version $Revision$
 */
public interface Discovery {
    final String ORG_OSGI_DISCOVERY = "org.osgi.discovery";

    final String ORG_OSGI_DISCOVERY_NONE = "none";

    final String ORG_OSGI_DISCOVERY_AUTO_PUBLISH = "auto-publish";

    /**
     * Add a ServiceListener for a particular service description.
     * 
     * @param serviceEndpointDescription
     *            describes the service(s) to listen for. If serviceEndpointDescription is <code>null</code> then all
     *            services are considered.
     * @param listener
     *            which is to call when discovery detects changes in availability or description of a service. The same
     *            listener object may also be used to listen on multiple serviceEndpointDescription objects.
     */
    void addServiceListener(ServiceListener listener, ServiceEndpointDescription serviceEndpointDescription);

    /**
     * Add a ServiceListener for a particular service description.
     * 
     * @param filter
     *            an LDAP filter which the service has to satisfy.
     * @param listener
     *            which is to call when discovery detects changes in a service's availability or description. The same
     *            listener object might also be used for multiple serviceEndpointDescription objects.
     */
    void addServiceListener(ServiceListener listener, String filter);

    /**
     * This method is the same as calling
     * <code>Discovery.addServiceListener(ServiceListener listener, ServiceEndpointDescription serviceEndpointDescription)</code>
     * with <code>serviceEndpointDescription</code> set to <code>null</code>.
     * 
     * @param listener
     *            which is to call when discovery detects changes in availability or description of a service. The same
     *            listener object may also be used to listen on multiple serviceEndpointDescription objects.
     * @see #addServiceListener(ServiceListener, ServiceEndpointDescription)
     */
    void addServiceListener(ServiceListener listener);

    /**
     * Removes a ServiceListener.
     * 
     * @param listener
     *            to remove. If that listener object haven't been added before, then the method returns without throwing
     *            exceptions.
     */
    void removeServiceListener(ServiceListener listener);

    /**
     * Looks for the given ServiceEndpointDescription in the cache. Cache might be local or global depending on the
     * cache strategy of the Discovery.<b>
     * 
     * @param serviceEndpointDescription
     *            to check for its existence in cache
     * @return true if a service matching the given serviceEndpointDescription is found in the local cache
     * @throws IllegalArgumentException
     *             if serviceEndpointDescription is null or incomplete
     */
    boolean isCached(ServiceEndpointDescription serviceEndpointDescription);

    /**
     * Returns an array of all ServiceEndpointDescription objects currently known to the Discovery implementation.
     * 
     * @return Array of ServiceEndpointDescription objects. An empty collection is returned if no service descriptions
     *         were found.
     */
    ServiceEndpointDescription[] getCachedServiceEndpointDescriptions();

    /**
     * Searches for services matching the criteria provided with the service description. Matching means in this context
     * a containment and not an equality relation though Discovery might choose to ignore some properties if it can't
     * really compare them e.g. if a property of the provided service description is a File object (wsdl file for
     * instance). For this reason its recommended to provide only simple properties with the service description object.
     * Any other, more complex matching operations could be done in the next step and are caller's responsibility.
     * 
     * @param serviceEndpointDescription
     *            ServiceEndpointDescription of the service to locate
     * @return Array of ServiceEndpointDescription objects satisfying the find criteria. The Collection may be empty if
     *         none was found
     * @throws IllegalArgumentException
     *             if serviceEndpointDescription is null.
     */
    ServiceEndpointDescription[] findService(ServiceEndpointDescription serviceEndpointDescription);

    /**
     * Find a service based on the provided LDAP filter.
     * 
     * @param filter
     *            an LDAP filter which the service has to satisfy.
     * @return Array of ServiceEndpointDescription objects matching the service that was found to satisfy the find
     *         criteria. The Collection may be empty if none was found.
     * @throws IllegalArgumentException
     *             if serviceEndpointDescription is null or incomplete
     * @throws UnsupportedOperationException
     *             if method is not supported by the implementation
     */
    ServiceEndpointDescription[] findService(String filter);

    /**
     * Asynchronous version of <code>Discovery.findService(ServiceEndpointDescription serviceEndpointDescription)</code>
     * method.
     * 
     * @param serviceEndpointDescription
     *            ServiceEndpointDescription of the service to locate
     * @param callback
     *            to notify about the asynchronous response of the find operation
     * @throws IllegalArgumentException
     *             if the serviceEndpointDescription or callback is null
     * @see #findService(ServiceEndpointDescription)
     */
    void findService(ServiceEndpointDescription serviceEndpointDescription, FindServiceCallback callback);

    /**
     * Asynchronous version of <code>Discovery.findService(String filter)</code> method.
     * 
     * @param filter
     *            an LDAP filter which the service has to satisfy.
     * @param callback
     *            to notify about the asynchronous response of the find operation
     * @throws IllegalArgumentException
     *             if the serviceEndpointDescription is null or incomplete
     * @throws UnsupportedOperationException
     *             if method is not supported by the implementation
     */
    void findService(String filter, FindServiceCallback callback);

    /**
     * Publish the provided service description. If the property org.osgi.discovery = auto-publish, the Discovery
     * implementation actively pushes the information about the service to the network. Otherwise, it is just available
     * upon request from other Discovery implementations.
     * 
     * @param serviceEndpointDescription
     *            ServiceEndpointDescription of the service to publish
     * @return true if the service was successfully published.
     * @throws IllegalArgumentException
     *             if serviceEndpointDescription is null, incomplete or invalid (e.g. contains unknown property types)
     */
    boolean publishService(ServiceEndpointDescription serviceEndpointDescription);

    /**
     * Publish the provided service. The information is published by the Discovery implementation.<b> If the parameter
     * autopublish=true, the Discovery implementation actively pushes the information about the service to the network.
     * Otherwise, it is just available upon request from other Discovery implementations. The ServiceEndpointDescription
     * is matched using the Comparable interface.
     * 
     * @param serviceEndpointDescription
     *            ServiceEndpointDescription of the service to publish
     * @param autopublish
     *            if true, service information is actively pushed to the network for discovery
     * @return true if the service was successfully published.
     * @throws IllegalArgumentException
     *             if serviceEndpointDescription is null, incomplete or invalid (e.g. contains unknown property types)
     */
    boolean publishService(ServiceEndpointDescription serviceEndpointDescription, boolean autopublish);

    /**
     * Make the given service un-discoverable. The previous publish request for a service is undone. The service
     * information is also removed from the local or global cache if cached before.
     * 
     * @param serviceEndpointDescription
     *            ServiceEndpointDescription of the service to unpublish. If this ServiceEndpointDescription haven't
     *            been published before, then the method returns without throwing exceptions.
     * @throws IllegalArgumentException
     *             if serviceEndpointDescription is null or incomplete
     */
    void unpublishService(ServiceEndpointDescription serviceEndpointDescription);
}
