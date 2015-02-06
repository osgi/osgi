/*
 * Copyright (c) OSGi Alliance (2013, 2015). All Rights Reserved.
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

package org.osgi.service.rest.client;
import java.net.URI;


/**
 * Factory to construct new REST client instances. Each instance is specific to
 * a REST service endpoint.
 * 
 * Implementations can choose to extend this interface to add additional
 * creation methods, e.g., where additional arguments are needed for request
 * signing, etc.
 * 
 * In OSGi environments this factory is registered as a service.
 * 
 * @author $Id$
 *
 */
public interface RestClientFactory {

	/**
	 * create a new REST client instance.
	 * 
	 * @param uri the URI to the REST service endpoint.
	 * @return a new REST client instance.
	 */
	RestClient createRestClient(URI uri);

}
