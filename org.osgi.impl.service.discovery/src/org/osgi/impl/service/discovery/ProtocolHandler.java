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

package org.osgi.impl.service.discovery;

import java.util.Collection;

import org.osgi.framework.Filter;
import org.osgi.service.discovery.ServiceEndpointDescription;

/**
 * 
 * This interface decouples the Discovery implementation from the specific
 * discovery protocol implementation.
 * 
 * @version $Revision$
 * @author Thomas Kiesslich
 */
public interface ProtocolHandler {

	//void registerServiceListenerMap();
	
	/**
	 * {@link org.osgi.service.discovery.Discovery#findService(ServiceEndpointDescription)}
	 */
	Collection findService(ServiceEndpointDescription serviceDescription);

	/**
	 * {@link org.osgi.service.discovery.Discovery#findService(String)}
	 */
	Collection findService(Filter filter);

	/**
	 * {@link org.osgi.service.discovery.Discovery#publishService(ServiceEndpointDescription, boolean)}
	 */
	boolean publishService(ServiceEndpointDescription serviceDescription,
			boolean autopublish);

	/**
	 * {@link org.osgi.service.discovery.Discovery#publishService(ServiceEndpointDescription)}
	 */
	boolean publishService(ServiceEndpointDescription serviceDescription);

	/**
	 * {@link org.osgi.service.discovery.Discovery#unpublishService(ServiceEndpointDescription)}
	 */
	void unpublishService(ServiceEndpointDescription serviceDescription);

	/**
	 * Called before shutdown of the ProtocolHandler to give it a chance to
	 * clean up resources.
	 */
	void destroy();

	/**
	 * Called by the Discovery service to initialize the ProtocolHandler
	 * specific implementation.
	 */
	void init();

}
