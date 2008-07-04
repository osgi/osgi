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

import java.util.Collection;

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
     * @param serviceDescription describes the service(s) to listen for. If serviceDescription is <code>null</code> 
     * then all services are considered.
     * @param listener which is to call when discovery detects changes in availability or description of a service. 
     * The same listener object may also be used to listen on multiple serviceDescription objects.
     */
    void addServiceListener(ServiceListener listener, ServiceDescription serviceDescription);
    
        
    /**
     * Add a ServiceListener for a particular service description. 
     * 
     * @param filter an LDAP filter which the service has to satisfy.
     * @param listener which is to call when discovery detects changes in a service's availability or description. 
     * The same listener object might also be used for multiple serviceDescription objects.
     */
    void addServiceListener(ServiceListener listener, String filter);

    
	/**
     * This method is the same as calling
     * <code>Discovery.addServiceListener(ServiceListener listener, ServiceDescription serviceDescription)</code>
     * with <code>serviceDescription</code> set to <code>null</code>.
     * 
     * @param listener which is to call when discovery detects changes in availability or description of a service. 
     * The same listener object may also be used to listen on multiple serviceDescription objects.
     * @see #addServiceListener(ServiceListener, ServiceDescription) 
	 */
	void addServiceListener(ServiceListener listener);
	
	
	/**
	 * Removes a ServiceListener. 
	 *  
	 * @param listener to remove. If that listener object haven't been added before, then the method returns without throwing exceptions. 
	 */
	void removeServiceListener(ServiceListener listener);
	
	
	/**
     * Looks for the given ServiceDescription in the cache. Cache might be local or global depending on the
     * cache strategy of the Discovery.<b>
     *      
     * @param serviceDescription to check for its existence in cache
     * @return true if a service matching the given serviceDescription is found in the local cache
     * @throws IllegalArgumentException if serviceDescription is null or incomplete
     */
    boolean isCached(ServiceDescription serviceDescription);
    
    
    /**
     * Returns an array of all ServiceDescription objects currently known to the Discovery implementation.
     * 
     * @return A collection of ServiceDescription objects. An empty collection is returned if no service descriptions
     * were found.
     */
    Collection getCachedServiceDescriptions();
    
	    	
	/**
	 * Searches for services matching the criteria provided with the service description. Matching means in this context 
	 * a containment and not an equality relation though Discovery might choose to ignore some properties if it can't really compare them 
	 * e.g. if a property of the provided service description is a File object (wsdl file for instance).
	 * For this reason its recommended to provide only simple properties with the service description object. 
	 * Any other, more complex matching operations could be done in the next step and are caller's responsibility.
	 *  
	 * @param serviceDescription ServiceDescription of the service to locate
	 * @return Collection of ServiceDescription objects satisfying the find criteria.
	 *         The Collection may be empty if none was found
	 * @throws IllegalArgumentException if serviceDescription is null.
	 */
	Collection findService(ServiceDescription serviceDescription);
	
    
    /**
     * Find a service based on the provided LDAP filter. 
     * 
     * @param filter an LDAP filter which the service has to satisfy.
     * @return Collection of ServiceDescription objects matching the service that was found to satisfy the find criteria.
     *         The Collection may be empty if none was found.
     * @throws IllegalArgumentException if serviceDescription is null or incomplete
     * @throws UnsupportedOperationException if method is not supported by the implementation
     */
    Collection findService(String filter);
	
    
    /**
     * Asynchronous version of <code>Discovery.findService(ServiceDescription serviceDescription)</code> method.
     * 
     * @param serviceDescription ServiceDescription of the service to locate
     * @param callback to notify about the asynchronous response of the find operation
     * @throws IllegalArgumentException if the serviceDescription or callback is null
     * @see #findService(ServiceDescription) 
     */
    void findService(ServiceDescription serviceDescription, FindServiceCallback callback);
  
    
    /**
     * Asynchronous interface to initiate the search for an suitable service based on the provided ServiceDescription and filter.
     * Discovery implementations might choose to not support this method if the discovery protocol doesn't support filtering.
     * The ServiceDescription is matched using the Comparable interface.
     * 
     * @param filter an LDAP filter which the service has to satisfy.
     * @param callback Listener object to notify about the asynchronous response of the find operation
     * @throws IllegalArgumentException if the serviceDescription is null or incomplete
     * @throws UnsupportedOperationException if method is not supported by the implementation
     */
    void findService(String filter, FindServiceCallback callback);
    
    
    /**
	 * Publish the provided service description.  
	 * If the property org.osgi.discovery = auto-publish, the Discovery implementation actively pushes the
	 * information about the service to the network. Otherwise, it is just available upon request from other
	 * Discovery implementations.
	 * 
	 * @param serviceDescription ServiceDescription of the service to publish
	 * @return true if the service was successfully published.
	 * @throws IllegalArgumentException if serviceDescription is null, incomplete or invalid (e.g. contains 
	 * unknown property types)
	 */
	boolean publishService(ServiceDescription serviceDescription);

	
	/**
	 * Publish the provided service. The information is published by the Discovery implementation.<b>
	 * If the parameter autopublish=true, the Discovery implementation actively pushes the
	 * information about the service to the network. Otherwise, it is just available upon request from other
	 * Discovery implementations.
	 * The ServiceDescription is matched using the Comparable interface.
	 * @param serviceDescription ServiceDescription of the service to publish
	 * @param autopublish if true, service information is actively pushed to the network for discovery
	 * @return true if the service was successfully published.
	 * @throws IllegalArgumentException if serviceDescription is null, incomplete or invalid (e.g. contains 
     * unknown property types)
     */
	boolean publishService(ServiceDescription serviceDescription, boolean autopublish);
	
	
	/**
	 * Make the given service un-discoverable. The previous publish request for a service is undone. The service
	 * information is also removed from the local or global cache if cached before.
	 * 
	 * @param serviceDescription ServiceDescription of the service to unpublish. If this ServiceDescription 
	 * haven't been published before, then the method returns without throwing exceptions.
	 * @throws IllegalArgumentException if serviceDescription is null or incomplete
	 */
	void unpublishService(ServiceDescription serviceDescription);
}
