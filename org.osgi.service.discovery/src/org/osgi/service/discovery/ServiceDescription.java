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

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

/**
 * The ServiceDescription interface defines the service and its properties. The main information necessary is the
 * name of the provided interface.<p/>
 * 
 * Implementations need to also implement the Comparator interface to determine whether two given
 * service descriptions are equal or close enough to say they match.<p/>
 * 
 * Semantically, <code>A.compare(B)</code> only applies if A and B are equal, meaning
 * <code>A.equals(B) == true</code>. In most case <code>A.compare(B) != B.compare(A)</code>. A could
 * be a subset of B and still be considered a match.<br/>
 * 
 * Implementations of the Comparator interface are encouraged to include additional information into
 * the determination process whether A and B are equal or compatible, e.g. cost, throughput, QoS.<p/>
 * 
 * Implementations of this interface are usually provided by the Distribution software.
 */
public interface ServiceDescription extends Comparator {
	/**
	 * @return The service interface name
	 */
	String getInterfaceName();
	
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
	Collection keys();
	
	/**
	 * @return Returns all properties of the interface as a <code>java.util.Map</code>.
	 */
	Map getProperties();
}
