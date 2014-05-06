/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

import java.net.URL;
import org.osgi.service.rest.client.RestClient.Signer;

/**
 * The RestClientFactory is used to create a new Java REST client instance
 * connected to a specific remote framework.
 * 
 * @author $Id$
 */
public interface RestClientFactory {

	/**
	 * Create a new Java REST client instance for a framework addressed by its
	 * URL.
	 * 
	 * @param url Addresses the REST interface of a remote OSGi framework to be
	 *        managed.
	 * @return Returns a new RestClient instance.
	 */
	RestClient createRestClient(URL url);

	/**
	 * Create a new Java REST client instance for a framework addressed by its
	 * URL.
	 * 
	 * @param url Addresses the REST interface of a remote OSGi framework to be
	 *        managed.
	 * @param signer Passes a Signer instance that is called back for every REST
	 *        request that the client makes so that the Signer can pass
	 *        additional headers for the Http request.
	 * @return Returns a new RestClient instance.
	 */
	RestClient createRestClient(URL url, Signer signer);

}
