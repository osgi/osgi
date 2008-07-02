/*
 * $Date: 2008-04-02 12:42:59 -0800 $
 *
 * Copyright (c) OSGi Alliance (2004, 2007). All Rights Reserved.
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

import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * The ServiceDescription interface defines the service and its properties. The main information necessary is the
 * name of the provided interface.<p/>
 * 
 * It's strongly recommended to override <code>Object.equals<code> method to implement an appropriate equivalence 
 * comparison for ServiceDescription objects. 
 * 
 * @version $Revision: 4930 $
 */
public interface ServiceDescription {
        
    /**
     * If value of <code>getInterfaceName</code> needs to be described as a key-value pair e.g. by the discovery 
     * protocol, filter for discovery etc. and there is no other key standardized for that purpose yet, then this is the 
     * recommended property key to use. 
     */    
    public final String PROP_KEY_INTERFACE_NAME = "interface-name";
    
    /**
     * If value of <code>getProtocolSpecificInterfaceName</code> needs to be described as a key-value pair e.g. by the discovery 
     * protocol, filter for discovery etc. and there is no other key standardized for that purpose yet, then this is the 
     * recommended property key to use. 
     */    
    public final String PROP_KEY_PROTOCOL_SPECIFIC_INTERFACE_NAME = "protocol-interface-name";
    
    /**
     * If value of <code>getVersion</code> needs to be described as a key-value pair e.g. by the discovery 
     * protocol, filter for discovery etc. and there is no other key standardized for that purpose yet, then this is the 
     * recommended property key to use. 
     */    
    public final String PROP_KEY_VERSION = "version";
    
    /**
     * If value of <code>getServiceLocation</code> needs to be described as a key-value pair e.g. by the discovery 
     * protocol, filter for discovery etc. and there is no other key standardized for that purpose yet, then this is the 
     * recommended property key to use. 
     */    
    public final String PROP_KEY_SERVICE_LOCATION = "location";
    
    /**
	 * @return The full qualified service interface name
	 */
	String getInterfaceName();
	
	/**
     * @return The protocol specific service interface name.   It might be for instance a web service interface name. 
     * Though this information is usually contained in according interface descriptions, e.g. a wsdl file, 
     * it might be provided here as well since discovery usually doesn't read and interprets such accompanying 
     * descriptions. 
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
	 * @param key Name of the property
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
