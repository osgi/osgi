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

package org.osgi.service.jdkhttp.whiteboard;

/**
 * Defines standard constants for the JDK HttpServer Whiteboard services.
 * <p>
 * Handlers, filters, and authenticators are registered as OSGi services with
 * these properties to configure how a JDK HttpServer Whiteboard
 * implementation picks them up.
 *
 * @author $Id$
 */
public final class JdkHttpWhiteboardConstants {
	private JdkHttpWhiteboardConstants() {
		// non-instantiable
	}

	/**
	 * Service property specifying the context path of a
	 * {@code com.sun.net.httpserver.HttpHandler} service.
	 * <p>
	 * The value must be a {@code String} starting with {@code /}. This
	 * property is <em>required</em> for a handler to be registered.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_CONTEXT_PATH					= "osgi.http.jdk.context.path";

	/**
	 * Optional service property for a human-readable name of a
	 * {@code com.sun.net.httpserver.HttpHandler} context.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_CONTEXT_NAME					= "osgi.http.jdk.context.name";

	/**
	 * Service property specifying the context path patterns of a
	 * {@code com.sun.net.httpserver.Filter} service.
	 * <p>
	 * The value is a {@code String} or {@code String[]} of context path
	 * patterns. Use {@code *} to match all contexts.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_FILTER_PATTERN					= "osgi.http.jdk.filter.pattern";

	/**
	 * Optional service property for a human-readable name of a
	 * {@code com.sun.net.httpserver.Filter} service.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_FILTER_NAME					= "osgi.http.jdk.filter.name";

	/**
	 * Service property specifying the context path patterns of a
	 * {@code com.sun.net.httpserver.Authenticator} service.
	 * <p>
	 * The value is a {@code String} or {@code String[]} of context path
	 * patterns.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_AUTHENTICATOR_PATTERN			= "osgi.http.jdk.authenticator.pattern";

	/**
	 * Optional service property for the authentication realm of a
	 * {@code com.sun.net.httpserver.Authenticator} service.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_AUTHENTICATOR_REALM			= "osgi.http.jdk.authenticator.realm";

	/**
	 * Service property specifying the context path patterns at which
	 * bundle resources are served.
	 * <p>
	 * The value is a {@code String} or {@code String[]} of context paths,
	 * each starting with {@code /}. A service registered with this property
	 * causes the whiteboard to serve entries of the registering bundle below
	 * the {@link #JDK_HTTP_RESOURCE_PREFIX prefix} at these paths. The
	 * service object itself is never used and can be any object.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_RESOURCE_PATTERN				= "osgi.http.jdk.resource.pattern";

	/**
	 * Service property specifying the bundle entry prefix from which
	 * resources are served.
	 * <p>
	 * The value is a {@code String} that must not end with {@code /}, except
	 * for {@code "/"} denoting the root of the bundle. A request below a
	 * {@link #JDK_HTTP_RESOURCE_PATTERN pattern} is mapped to the bundle
	 * entry {@code prefix + relative path}.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_RESOURCE_PREFIX				= "osgi.http.jdk.resource.prefix";

	/**
	 * Optional service property used to target a specific JDK HttpServer
	 * Whiteboard implementation.
	 * <p>
	 * The value is an LDAP filter expression that is matched against the
	 * properties of the
	 * {@code org.osgi.service.jdkhttp.runtime.JdkHttpServerRuntime} service.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_WHITEBOARD_TARGET				= "osgi.http.jdk.target";

	/**
	 * The name of the JDK HttpServer Whiteboard implementation capability.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_WHITEBOARD_IMPLEMENTATION		= "osgi.http.jdk";

	/**
	 * The version of the JDK HttpServer Whiteboard Specification implemented
	 * by this bundle.
	 * <p>
	 * Value: {@value}
	 */
	public static final String	JDK_HTTP_WHITEBOARD_SPECIFICATION_VERSION	= "1.0";
}
