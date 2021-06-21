/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.rest.client;

import java.net.URI;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Factory to construct new REST client instances. Each instance is specific to
 * a REST service endpoint.
 * 
 * <p>
 * Implementations can choose to extend this interface to add additional
 * creation methods, where additional arguments are needed for request signing,
 * etc.
 * 
 * <p>
 * In OSGi environments, this factory is registered as a service.
 * 
 * @author $Id$
 */
@ProviderType
public interface RestClientFactory {

	/**
	 * Create a new REST client instance.
	 * 
	 * @param uri The URI to the REST service endpoint.
	 * @return A new REST client instance for the specified REST service
	 *         endpoint.
	 */
	RestClient createRestClient(URI uri);
}
