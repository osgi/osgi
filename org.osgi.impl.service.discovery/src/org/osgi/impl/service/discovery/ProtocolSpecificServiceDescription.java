/*
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

package org.osgi.impl.service.discovery;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * The ServiceEndpointDescription interface describes an endpoint of a service. This class can be considered as a
 * wrapper around the property map associated with a service and its endpoint. It provides an API to conveniently access
 * the most important properties of the service.
 * 
 * It's strongly recommended to override <code>Object.equals<code> method to implement an appropriate equivalence 
 * comparison for ServiceEndpointDescription objects. 
 * 
 * @version $Revision: 5654 $
 */
public interface ProtocolSpecificServiceDescription {

    /**
     * @return full qualified service interface name provided by the advertised service (endpoint).
     */
    String getInterfaceName();

    /**
     * 
     * @return The protocol specific service interface name.
     */
    String getProtocolSpecificInterfaceName();

    /**
     * @return The service interface/implementation version.
     */
    String getVersion();

    /**
     * @return The URL of the service location.
     */
    URL getLocation();

    /**
     * Getter method for the property value of a given key.
     * 
     * @param key
     *            Name of the property
     * @return The property value, null if none is found for the given key
     */
    Object getProperty(String key);

    /**
     * @return <code>java.util.Collection</code> of the property names available in the ServiceDescription
     */
    Collection getPropertyKeys();

    /**
     * @return Returns all properties of the service as a <code>java.util.Map</code>.
     */
    Map getProperties();
}
