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
 * TODO: consider whether methods should also contain a source object (the particular discovery service which does the
 * notification)
 * 
 * @version $Revision$
 */
public interface ServiceListener {
    /**
     * Callback indicating that the specified service was discovered and is known to the calling Discovery
     * implementation.
     * 
     * @param serviceEndpointDescription
     */
    void serviceAvailable(ServiceEndpointDescription serviceEndpointDescription);

    /**
     * Callback indicating a change in the service endpoint description of a previously discovered service.
     * 
     * @param oldDescription
     *            previous service endpoint description
     * @param newDescription
     *            new service endpoint description
     */
    void serviceModified(ServiceEndpointDescription oldDescription, ServiceEndpointDescription newDescription);

    /**
     * Callback indicating that the specified service endpoint is no longer available.
     * 
     * @param serviceEndpointDescription
     *            ServiceEndpointDescription of the service that is no longer available
     */
    void serviceUnavailable(ServiceEndpointDescription serviceEndpointDescription);
}
