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

/**
 *
 * TODO: consider whether methods should also contain a source object (the particular discovery service which does the notification)
 * 
 * @version $Revision: 4930 $
 */
public interface ServiceListener {
	/**
	 * Callback indicating that the specified service was discovered and is known to the calling Discovery implementation.
	 * @param serviceDescription
	 */
	void serviceAvailable(ServiceDescription serviceDescription);
	
	/**
	 * Callback indicating a change in the service description of a previously discovered service.
	 * @param oldDescription previous service description
	 * @param newDescription new service description
	 */
	void serviceModified(ServiceDescription oldDescription, ServiceDescription newDescription);
	
	/**
	 * Callback indicating that the specified service is no longer available/
	 * @param serviceDescription ServiceDescription of the service that is no longer available
	 */
	void serviceUnavailable(ServiceDescription serviceDescription);
}
