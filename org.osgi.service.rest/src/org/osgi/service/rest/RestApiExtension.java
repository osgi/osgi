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

package org.osgi.service.rest;

/**
 * Marker interface for registering extensions to the Rest API service.
 * 
 * <p>
 * The REST service provides a RESTful interface to clients that need to manage
 * an OSGi framework through a network connection. Other components running
 * on the same framework can contribute their own specific REST
 * interface and make it available and discoverable by registering this marker
 * service using the Whiteboard pattern.
 * </p>
 * <p>
 * Integration of third-party REST interfaces with the framework REST service on
 * the implementation level might not always be possible since it requires
 * knowledge about the underlying implementation and an extension mechanism on
 * that level. Specific technologies such as servlets might support
 * this but the REST service could as well be implemented without the use of a
 * supporting abstraction layer and not offer extensibility.
 * </p>
 * <p>
 * Using this marker service, the REST service includes the advertised service
 * in the Extensions Resource, allowing clients to discover it and 
 * use the extension's functionality.
 * </p>
 * 
 * @author $Id$
 */
public interface RestApiExtension {

	/**
	 * This service property describes a URI to the REST extension on this local
	 * machine. It is either an fully qualified URI with a different port if no
	 * integration with the framework REST service is possible or a relative URI
	 * implicitly using the same port if integration is possible. 
	 * The type of this property is
	 * <code>java.lang.String</code> and the property is mandatory.
	 */
	public static final String	URI_PATH	= "org.osgi.rest.uri.path";

	/**
	 * This service property describes the package name of the technology 
         * manageable by this REST API extension. Services specified in OSGi
	 * specifications must use their canonical package name as the name.
	 * Third-party technologies should also use their package names. The type of this
	 * property is <code>java.lang.String</code> and the property is mandatory.
	 */
	public static final String	NAME		= "org.osgi.rest.name";

	/**
	 * This service property refers to the id of the service the REST API
	 * extension provides management capabilities for. This can be useful if more
         * than one service of a given type is present in the framework. For example
         * if more than one Http Service is available this property is used to 
         * associate a REST extension managing the Http Service with a specific 
         * service instance.
	 * The type of the property is
	 * <code>java.lang.Long</code> and the property is optional; if the REST
         * extension is not directly associated with a service in the service 
         * registry, the property should not be set.
	 */
	public static final String	SERVICE		= "org.osgi.rest.service";

}
