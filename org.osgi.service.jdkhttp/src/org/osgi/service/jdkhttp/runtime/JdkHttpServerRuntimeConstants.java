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

package org.osgi.service.jdkhttp.runtime;

/**
 * Defines standard names for JDK HttpServer Runtime Service constants.
 *
 * @author $Id$
 */
public final class JdkHttpServerRuntimeConstants {
	private JdkHttpServerRuntimeConstants() {
		// non-instantiable
	}

	/**
	 * JDK HttpServer Runtime Service service property specifying the
	 * endpoints upon which the JDK HttpServer Whiteboard implementation is
	 * listening.
	 * <p>
	 * An endpoint value is a URL to which the JDK HttpServer Whiteboard
	 * implementation is listening, for example
	 * {@code http://192.168.1.10:8080/} or {@code https://localhost:8443/}.
	 * <p>
	 * A JDK HttpServer Whiteboard implementation can be listening on multiple
	 * endpoints.
	 * <p>
	 * The value of this service property must be of type {@code String},
	 * {@code String[]}, or {@code Collection<String>}.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_ENDPOINT	= "osgi.http.jdk.endpoint";
}
