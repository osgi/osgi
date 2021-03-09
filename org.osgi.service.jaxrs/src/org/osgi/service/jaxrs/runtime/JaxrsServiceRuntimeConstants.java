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

package org.osgi.service.jaxrs.runtime;

/**
 * Defines standard names for JAX-RS Runtime Service constants.
 * 
 * @author $Id$
 */
public final class JaxrsServiceRuntimeConstants {
	private JaxrsServiceRuntimeConstants() {
		// non-instantiable
	}

	/**
	 * JAX-RS Runtime Service service property specifying the endpoints upon
	 * which the JAX-RS implementation is available.
	 * 
	 * <p>
	 * An endpoint value is a URL or a relative path, to which the JAX-RS
	 * Whiteboard implementation is listening. For example,
	 * {@code http://192.168.1.10:8080/} or {@code /myapp/}. A relative path may
	 * be used if the scheme and authority parts of the URL are not known, e.g.
	 * if a bridged Http Whiteboard implementation is used. If the JAX-RS
	 * Whiteboard implementation is serving the root context and neither scheme
	 * nor authority is known, the value of the property is "/". Both, a URL and
	 * a relative path, must end with a slash.
	 * <p>
	 * A JAX-RS Whiteboard implementation can be listening on multiple
	 * endpoints.
	 * 
	 * <p>
	 * The value of this service property must be of type {@code String},
	 * {@code String[]}, or {@code Collection<String>}.
	 */
	public static final String JAX_RS_SERVICE_ENDPOINT = "osgi.jaxrs.endpoint";

}
