/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

import org.osgi.framework.ServiceReference;

/**
 * An Import Reference associates an active proxy service to a remote endpoint.
 * 
 * The Import Reference can be used to reference an imported service. When the
 * service is no longer imported, all methods must return <code>null</code>;
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface ImportReference {
	/**
	 * Answer the associated Service Reference for the proxy to the endpoint.
	 * 
	 * @return A Service Reference to the proxy for the endpoint.
	 */
	ServiceReference getImportedService();

	/**
	 * Answer the associated remote Endpoint Description.
	 * 
	 * @return A Endpoint Description for the remote endpoint.
	 */
	EndpointDescription getImportedEndpoint();
}
